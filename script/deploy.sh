#!/bin/bash
set -e

cd /home/ubuntu

NGINX_CONF="/home/ubuntu/nginx/default.conf"
NGINX_DIR="/home/ubuntu/nginx"

write_nginx_config() {
    local slot=$1
    mkdir -p "$NGINX_DIR"
    cat > "$NGINX_CONF" << EOF
resolver 127.0.0.11 valid=10s ipv6=off;

server {
    listen 80;
    server_name _;

    location / {
        set \$backend "floney-app-${slot}:8080";
        proxy_pass http://\$backend;
        proxy_http_version 1.1;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header Connection "";
        proxy_read_timeout 60s;
    }
}
EOF
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

# 현재 실행 중인 슬롯 감지
CURRENT=""
if docker ps --format '{{.Names}}' | grep -q "^floney-app-blue$"; then
    CURRENT="blue"
elif docker ps --format '{{.Names}}' | grep -q "^floney-app-green$"; then
    CURRENT="green"
fi

# ── 첫 번째 배포 ──────────────────────────────────────────────────
if [ -z "$CURRENT" ]; then
    echo "🚀 첫 번째 배포 시작..."

    docker compose up -d redis

    write_nginx_config "blue"
    docker compose up -d app-blue

    wait_healthy "floney-app-blue" || exit 1

    docker compose up -d nginx

    echo "🎉 첫 번째 배포 완료! 활성 슬롯: blue"
    exit 0
fi

# ── Blue-Green 배포 ───────────────────────────────────────────────
NEXT=$([ "$CURRENT" = "blue" ] && echo "green" || echo "blue")
echo "🔄 배포 시작: $CURRENT → $NEXT"

# 이전 배포 잔여 컨테이너 정리
docker compose stop app-$NEXT 2>/dev/null || true

# 새 이미지 pull
docker compose pull app-$NEXT

# 새 슬롯 시작
echo "▶️  floney-app-$NEXT 시작..."
docker compose up -d app-$NEXT

# 헬스체크 대기
wait_healthy "floney-app-$NEXT" || {
    docker compose stop app-$NEXT || true
    echo "❌ 롤백: 기존 슬롯($CURRENT) 유지"
    exit 1
}

# nginx 설정 업데이트 → 무중단 reload
echo "🔀 nginx → $NEXT 전환..."
write_nginx_config "$NEXT"
docker exec floney-nginx nginx -t
docker exec floney-nginx nginx -s reload
echo "✅ nginx → floney-app-$NEXT 전환 완료 (무중단)"

# 구 슬롯 종료
echo "⏹️  floney-app-$CURRENT 종료..."
docker compose stop app-$CURRENT

# 구 이미지 정리
docker image prune -f || true

echo "🎉 무중단 배포 완료! 활성 슬롯: floney-app-$NEXT"
