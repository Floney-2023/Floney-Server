#!/bin/bash
set -e

cd /home/ubuntu

COMPOSE_FILE="${COMPOSE_FILE:-docker-compose.yml}"
UPSTREAM_CONF="/etc/nginx/conf.d/floney-upstream.conf"
NGINX_SITE="/etc/nginx/sites-enabled/default"
BLUE_PORT=8081
GREEN_PORT=8082

echo "📋 사용 compose 파일: $COMPOSE_FILE"

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
    echo "✅ nginx reload 완료"
}

wait_healthy() {
    local container=$1
    echo "⏳ $container 헬스체크 대기..."
    for i in $(seq 1 50); do
        STATUS=$(docker inspect --format='{{.State.Health.Status}}' "$container" 2>/dev/null || echo "none")
        if [ "$STATUS" = "healthy" ]; then
            echo "✅ $container 정상 기동!"
            return 0
        fi
        [ "$i" -eq 50 ] && { echo "❌ 헬스체크 타임아웃 ($container)"; return 1; }
        echo "대기 중... ($i/50) status=$STATUS"
        sleep 5
    done
}

get_port() {
    [ "$1" = "blue" ] && echo $BLUE_PORT || echo $GREEN_PORT
}

# ── system nginx 초기화 (최초 1회: proxy_pass → upstream 방식으로 전환) ──
setup_nginx_once() {
    if ! grep -q "floney_backend" "$NGINX_SITE"; then
        echo "🔧 system nginx upstream 방식으로 전환 (최초 1회)..."
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

# ── 메인 ──────────────────────────────────────────────────────────

setup_nginx_once

CURRENT=$(get_current_slot)

# 첫 번째 배포 (실행 중인 앱 컨테이너 없음)
if ! docker ps --format '{{.Names}}' | grep -qE "^floney-app-(blue|green)$"; then
    echo "🚀 첫 번째 배포 시작..."

    # 기존 standalone 컨테이너 정리 (compose로 전환)
    echo "🧹 기존 standalone 컨테이너 정리..."
    docker stop floney-app 2>/dev/null || true
    docker rm floney-app 2>/dev/null || true
    docker stop floney-redis 2>/dev/null || true
    docker rm floney-redis 2>/dev/null || true

    docker compose -f "$COMPOSE_FILE" up -d --remove-orphans redis
    docker compose -f "$COMPOSE_FILE" up -d --remove-orphans app-blue

    wait_healthy "floney-app-blue" || exit 1

    write_upstream $BLUE_PORT
    reload_nginx

    echo "🎉 첫 번째 배포 완료! 활성 슬롯: blue (포트: $BLUE_PORT)"
    exit 0
fi

# Blue-Green 배포
NEXT=$([ "$CURRENT" = "blue" ] && echo "green" || echo "blue")
NEXT_PORT=$(get_port $NEXT)
CURRENT_PORT=$(get_port $CURRENT)
echo "🔄 배포 시작: $CURRENT ($CURRENT_PORT) → $NEXT ($NEXT_PORT)"

# 이전 배포 잔여 컨테이너 정리
docker compose -f "$COMPOSE_FILE" stop app-$NEXT 2>/dev/null || true

# 새 이미지 pull
docker compose -f "$COMPOSE_FILE" pull app-$NEXT

# 새 슬롯 시작
echo "▶️  floney-app-$NEXT 시작 (포트 $NEXT_PORT)..."
docker compose -f "$COMPOSE_FILE" up -d --remove-orphans app-$NEXT

# 헬스체크 대기
wait_healthy "floney-app-$NEXT" || {
    docker compose -f "$COMPOSE_FILE" stop app-$NEXT || true
    echo "❌ 롤백: 기존 슬롯($CURRENT:$CURRENT_PORT) 유지"
    exit 1
}

# system nginx upstream 전환 → 무중단 reload
echo "🔀 nginx upstream → 127.0.0.1:$NEXT_PORT 전환..."
write_upstream $NEXT_PORT
reload_nginx
echo "✅ nginx → floney-app-$NEXT:$NEXT_PORT 전환 완료 (무중단)"

# 구 슬롯 종료
echo "⏹️  floney-app-$CURRENT 종료..."
docker compose -f "$COMPOSE_FILE" stop app-$CURRENT

# 구 이미지 정리
docker image prune -f || true

echo "🎉 무중단 배포 완료! 활성 슬롯: floney-app-$NEXT (포트: $NEXT_PORT)"
