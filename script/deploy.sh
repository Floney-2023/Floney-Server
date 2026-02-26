#!/bin/bash
set -e

cd /home/ubuntu

DEPLOY_LOG="/home/ubuntu/deploy.log"
UPSTREAM_CONF="/etc/nginx/conf.d/floney-upstream.conf"
NGINX_SITE="/etc/nginx/sites-enabled/default"
BLUE_PORT=8081
GREEN_PORT=8082
HEALTH_URL="http://localhost"
HEALTH_PATH="/health-check"

# ── 로깅 ──────────────────────────────────────────────────────────

log() {
    local msg="[$(date '+%Y-%m-%d %H:%M:%S')] $1"
    echo "$msg"
    echo "$msg" >> "$DEPLOY_LOG"
}

# ── 헬퍼 함수 ─────────────────────────────────────────────────────

write_upstream() {
    local port=$1
    sudo tee "$UPSTREAM_CONF" > /dev/null << EOF
upstream floney_backend {
    server 127.0.0.1:${port};
}
EOF
}

reload_nginx() {
    sudo nginx -t && {
        if sudo systemctl is-active --quiet nginx; then
            sudo systemctl reload nginx
        else
            sudo systemctl start nginx
        fi
    }
    log "✅ nginx reload 완료"
}

# 직접 HTTP 프로브 (Docker 헬스체크 대신 즉시 감지)
wait_healthy_http() {
    local container=$1
    local port=$2
    local max_attempts=36  # 36 * 5s = 180s (3분)
    log "⏳ $container HTTP 헬스체크 대기 (port=$port)..."

    for i in $(seq 1 $max_attempts); do
        # 컨테이너 crash 감지
        if ! docker ps --format '{{.Names}}' | grep -q "^${container}$"; then
            log "❌ $container 컨테이너가 종료됨! 로그 확인:"
            docker logs --tail 30 "$container" 2>&1 | tee -a "$DEPLOY_LOG"
            return 1
        fi

        # HTTP 프로브
        HTTP_STATUS=$(curl -s -o /dev/null -w '%{http_code}' "${HEALTH_URL}:${port}${HEALTH_PATH}" 2>/dev/null || echo "000")
        if [ "$HTTP_STATUS" = "200" ]; then
            log "✅ $container 정상 기동! (${i}번째 시도, HTTP $HTTP_STATUS)"
            return 0
        fi

        log "대기 중... ($i/$max_attempts) HTTP=$HTTP_STATUS"
        sleep 5
    done

    log "❌ 헬스체크 타임아웃 ($container, ${max_attempts}회 시도)"
    docker logs --tail 30 "$container" 2>&1 | tee -a "$DEPLOY_LOG"
    return 1
}

get_port() {
    [ "$1" = "blue" ] && echo $BLUE_PORT || echo $GREEN_PORT
}

# ── system nginx 초기화 (최초 1회: proxy_pass → upstream 방식으로 전환) ──
setup_nginx_once() {
    if ! grep -q "floney_backend" "$NGINX_SITE"; then
        log "🔧 system nginx upstream 방식으로 전환 (최초 1회)..."
        write_upstream $BLUE_PORT
        sudo sed -i 's|proxy_pass http://127.0.0.1:8080;|proxy_pass http://floney_backend;|g' "$NGINX_SITE"
        reload_nginx
    fi
}

# ── 현재 활성 슬롯 감지 (upstream.conf 기준) ──
get_current_slot() {
    if [ -f "$UPSTREAM_CONF" ] && grep -q ":$GREEN_PORT" "$UPSTREAM_CONF"; then
        echo "green"
    else
        echo "blue"
    fi
}

# ── 배포 전 메모리 확보 ──
free_memory() {
    log "🧹 배포 전 메모리 확보..."
    docker image prune -f 2>/dev/null || true
    # page cache drop (root 권한 필요)
    sudo sh -c 'echo 3 > /proc/sys/vm/drop_caches' 2>/dev/null || true
    log "메모리 상태: $(free -m | grep Mem | awk '{printf \"전체=%sMB 사용=%sMB 가용=%sMB\", $2, $3, $7}')"
}

# ── 메인 ──────────────────────────────────────────────────────────

log "═══════════════════════════════════════════════════════════"
log "🚀 배포 시작"

setup_nginx_once

CURRENT=$(get_current_slot)

# 첫 번째 배포 (실행 중인 앱 컨테이너 없음)
if ! docker ps --format '{{.Names}}' | grep -qE "^floney-app-(blue|green)$"; then
    log "🚀 첫 번째 배포 시작..."

    free_memory

    docker compose up -d --remove-orphans redis
    docker compose up -d --remove-orphans app-blue

    wait_healthy_http "floney-app-blue" $BLUE_PORT || exit 1

    write_upstream $BLUE_PORT
    reload_nginx

    # 구 floney-app 컨테이너 정리
    docker stop floney-app 2>/dev/null || true
    docker rm floney-app 2>/dev/null || true

    log "🎉 첫 번째 배포 완료! 활성 슬롯: blue (포트: $BLUE_PORT)"
    exit 0
fi

# ── Blue-Green 배포 ──

NEXT=$([ "$CURRENT" = "blue" ] && echo "green" || echo "blue")
NEXT_PORT=$(get_port $NEXT)
CURRENT_PORT=$(get_port $CURRENT)
log "🔄 배포 시작: $CURRENT ($CURRENT_PORT) → $NEXT ($NEXT_PORT)"

# 배포 전 메모리 확보
free_memory

# 이전 배포 잔여 컨테이너 정리
docker compose stop app-$NEXT 2>/dev/null || true

# 새 이미지 pull
docker compose pull app-$NEXT

# 새 슬롯 시작
log "▶️  floney-app-$NEXT 시작 (포트 $NEXT_PORT)..."
docker compose up -d --remove-orphans app-$NEXT

# 직접 HTTP 헬스체크
wait_healthy_http "floney-app-$NEXT" $NEXT_PORT || {
    docker compose stop app-$NEXT || true
    log "❌ 롤백: 기존 슬롯($CURRENT:$CURRENT_PORT) 유지"
    exit 1
}

# nginx upstream 전환
log "🔀 nginx upstream → 127.0.0.1:$NEXT_PORT 전환..."
write_upstream $NEXT_PORT
reload_nginx || {
    # nginx reload 실패 시 upstream 롤백
    log "❌ nginx reload 실패! upstream 롤백 → $CURRENT_PORT"
    write_upstream $CURRENT_PORT
    reload_nginx
    docker compose stop app-$NEXT || true
    exit 1
}
log "✅ nginx → floney-app-$NEXT:$NEXT_PORT 전환 완료"

# 커넥션 드레이닝 (기존 연결이 완료될 때까지 대기)
log "⏳ 커넥션 드레이닝 (5초 대기)..."
sleep 5

# 전환 후 재검증 (새 컨테이너가 실제 트래픽에서 정상인지 확인)
log "🔍 전환 후 재검증..."
VERIFY_STATUS=$(curl -s -o /dev/null -w '%{http_code}' "${HEALTH_URL}:${NEXT_PORT}${HEALTH_PATH}" 2>/dev/null || echo "000")
if [ "$VERIFY_STATUS" != "200" ]; then
    log "❌ 전환 후 검증 실패 (HTTP=$VERIFY_STATUS), 롤백..."
    write_upstream $CURRENT_PORT
    reload_nginx
    docker compose stop app-$NEXT || true
    exit 1
fi
log "✅ 전환 후 재검증 성공 (HTTP=$VERIFY_STATUS)"

# 구 슬롯 종료
log "⏹️  floney-app-$CURRENT 종료..."
docker compose stop app-$CURRENT

# 구 이미지 정리
docker image prune -f || true

log "🎉 무중단 배포 완료! 활성 슬롯: floney-app-$NEXT (포트: $NEXT_PORT)"
log "═══════════════════════════════════════════════════════════"
