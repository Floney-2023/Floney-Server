# OCI HTTPS 설정 가이드 (Let's Encrypt SSL)

이 문서는 OCI (Oracle Cloud Infrastructure) 서버에 Nginx와 Let's Encrypt SSL 인증서를 설정하는 전체 과정을 설명합니다.

## 📋 목차

1. [사전 준비사항](#사전-준비사항)
2. [방화벽 설정](#방화벽-설정)
3. [Nginx 및 Certbot 설치](#nginx-및-certbot-설치)
4. [DNS 설정](#dns-설정)
5. [SSL 인증서 발급](#ssl-인증서-발급)
6. [Nginx 설정](#nginx-설정)
7. [자동 갱신 확인](#자동-갱신-확인)
8. [검증 및 테스트](#검증-및-테스트)
9. [트러블슈팅](#트러블슈팅)

---

## 사전 준비사항

### 필요한 정보
- OCI 서버 IP 주소
- SSH 접속 키
- 도메인 이름 (예: floney.store)
- 관리자 이메일 (Let's Encrypt 알림용)

### 환경
- OS: Ubuntu 22.04 LTS
- Spring Boot 애플리케이션 (포트 8080)
- Docker 및 Docker Compose 설치됨

---

## 방화벽 설정

### 1. iptables 규칙 추가

OCI 인스턴스는 기본적으로 SSH(22)만 열려있으므로 HTTP(80), HTTPS(443) 포트를 열어야 합니다.

```bash
# SSH 접속
ssh -i ~/path/to/ssh-key ubuntu@<OCI_IP>

# 포트 80, 443 열기
sudo iptables -I INPUT 1 -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT 1 -p tcp --dport 443 -j ACCEPT

# 규칙 확인
sudo iptables -L -n -v | head -20

# 규칙 저장 (재부팅 후에도 유지)
sudo netfilter-persistent save
```

### 2. OCI 보안 목록(Security List) 설정

OCI 콘솔에서 추가 설정이 필요할 수 있습니다:

1. OCI Console → Networking → Virtual Cloud Networks
2. 해당 VCN 선택 → Security Lists
3. Default Security List 선택 → Ingress Rules 추가

**추가할 규칙:**
```
Source CIDR: 0.0.0.0/0
IP Protocol: TCP
Destination Port Range: 80
Description: HTTP for Let's Encrypt

Source CIDR: 0.0.0.0/0
IP Protocol: TCP
Destination Port Range: 443
Description: HTTPS
```

---

## Nginx 및 Certbot 설치

```bash
# 패키지 목록 업데이트
sudo apt-get update

# Nginx, Certbot, Certbot Nginx 플러그인 설치
sudo apt-get install -y nginx certbot python3-certbot-nginx

# Nginx 상태 확인
sudo systemctl status nginx

# Nginx 자동 시작 설정
sudo systemctl enable nginx
```

---

## DNS 설정

SSL 인증서를 발급받기 **전에** 도메인이 OCI 서버 IP를 가리켜야 합니다.

### 도메인 관리 콘솔에서 설정

**예시: Route53, Cloudflare, Gabia 등**

1. A 레코드 추가/수정:
   ```
   이름: @ (또는 루트 도메인)
   타입: A
   값: <OCI_IP>
   TTL: 300 (5분) 또는 자동
   ```

2. www 서브도메인 추가 (선택사항):
   ```
   이름: www
   타입: A
   값: <OCI_IP>
   TTL: 300
   ```

### DNS 전파 확인

```bash
# 로컬 머신에서 실행
dig +short your-domain.com
dig +short www.your-domain.com

# 결과가 OCI IP와 일치해야 함
```

DNS 전파는 보통 5-10분 소요되지만, 최대 24-48시간이 걸릴 수 있습니다.

---

## SSL 인증서 발급

### 임시 Nginx 설정 생성

```bash
sudo tee /etc/nginx/sites-available/default > /dev/null << 'EOF'
server {
    listen 80;
    listen [::]:80;

    server_name your-domain.com www.your-domain.com;

    # ACME challenge
    location ^~ /.well-known/acme-challenge/ {
        root /var/www/html;
        default_type "text/plain";
    }

    # 임시 프록시 설정
    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

# ACME challenge 디렉토리 생성
sudo mkdir -p /var/www/html

# Nginx 설정 테스트 및 재시작
sudo nginx -t
sudo systemctl reload nginx
```

### Certbot으로 SSL 인증서 발급

```bash
# 단일 도메인
sudo certbot --nginx -d your-domain.com \
  --non-interactive \
  --agree-tos \
  --email your-email@example.com \
  --redirect

# 여러 도메인 (www 포함)
sudo certbot --nginx -d your-domain.com -d www.your-domain.com \
  --non-interactive \
  --agree-tos \
  --email your-email@example.com \
  --redirect
```

**옵션 설명:**
- `--nginx`: Nginx 플러그인 사용
- `-d`: 도메인 지정 (여러 개 가능)
- `--non-interactive`: 자동 실행 (프롬프트 없음)
- `--agree-tos`: Let's Encrypt 약관 동의
- `--email`: 만료 알림 이메일
- `--redirect`: HTTP를 HTTPS로 자동 리다이렉트 설정

### 발급 성공 메시지

```
Successfully received certificate.
Certificate is saved at: /etc/letsencrypt/live/your-domain.com/fullchain.pem
Key is saved at:         /etc/letsencrypt/live/your-domain.com/privkey.pem
This certificate expires on 2026-05-10.
Certbot has set up a scheduled task to automatically renew this certificate.
```

---

## Nginx 설정

### 최종 Nginx 설정 파일

Certbot이 자동으로 설정을 변경하지만, 더 깔끔하게 정리할 수 있습니다:

```bash
sudo tee /etc/nginx/sites-available/default > /dev/null << 'EOF'
############################################################
# 1) HTTP (80) → HTTPS 리다이렉트 + ACME challenge
############################################################
server {
    listen 80;
    listen [::]:80;

    server_name your-domain.com www.your-domain.com;

    # ACME challenge - Let's Encrypt 인증서 갱신용
    location ^~ /.well-known/acme-challenge/ {
        root /var/www/html;
        default_type "text/plain";
    }

    # 나머지 모든 요청은 HTTPS로 리다이렉트
    location / {
        return 301 https://$host$request_uri;
    }
}

############################################################
# 2) HTTPS (443) 설정
############################################################
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;

    server_name your-domain.com www.your-domain.com;

    # SSL 인증서 (Let's Encrypt)
    ssl_certificate     /etc/letsencrypt/live/your-domain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/your-domain.com/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    # Spring Boot 애플리케이션 리버스 프록시
    location / {
        proxy_pass http://127.0.0.1:8080;

        # 필수 헤더 설정
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 지원 (필요한 경우)
        # proxy_set_header Upgrade $http_upgrade;
        # proxy_set_header Connection "upgrade";
    }
}
EOF

# 설정 테스트
sudo nginx -t

# Nginx 재시작
sudo systemctl reload nginx
```

**주요 설정 설명:**
- `http2`: HTTP/2 프로토콜 활성화 (성능 향상)
- `ssl_certificate`: SSL 인증서 경로 (Let's Encrypt가 자동 생성)
- `proxy_pass`: Spring Boot 앱으로 프록시
- `proxy_set_header`: 클라이언트 정보를 백엔드로 전달

---

## 자동 갱신 확인

Let's Encrypt 인증서는 **90일마다 갱신**이 필요합니다. Certbot은 자동으로 갱신을 설정합니다.

### 자동 갱신 타이머 확인

```bash
# Certbot 타이머 상태 확인
sudo systemctl status certbot.timer

# 타이머가 활성화되어 있어야 함
sudo systemctl enable certbot.timer
```

### 수동 갱신 테스트

```bash
# Dry-run으로 갱신 프로세스 테스트 (실제 갱신 안 함)
sudo certbot renew --dry-run

# 실제 갱신 (30일 이내 만료 예정인 인증서만)
sudo certbot renew
```

### 인증서 정보 확인

```bash
# 현재 인증서 목록 및 만료일 확인
sudo certbot certificates
```

**출력 예시:**
```
Certificate Name: your-domain.com
  Serial Number: 5820b593d36cea0963c7046ae0feddbdb48
  Domains: your-domain.com www.your-domain.com
  Expiry Date: 2026-05-10 10:32:45+00:00 (VALID: 89 days)
  Certificate Path: /etc/letsencrypt/live/your-domain.com/fullchain.pem
  Private Key Path: /etc/letsencrypt/live/your-domain.com/privkey.pem
```

---

## 검증 및 테스트

### 1. HTTPS 연결 테스트

```bash
# 로컬 머신에서
curl -I https://your-domain.com

# 예상 결과: HTTP/2 200 또는 401 (인증 필요)
```

### 2. HTTP → HTTPS 리다이렉트 테스트

```bash
curl -I http://your-domain.com

# 예상 결과: 301 Moved Permanently
# Location: https://your-domain.com/
```

### 3. SSL 인증서 검증

브라우저에서 확인:
1. `https://your-domain.com` 접속
2. 주소창의 자물쇠 아이콘 클릭
3. 인증서 정보 확인:
   - 발급자: Let's Encrypt
   - 유효기간: 90일
   - 도메인: your-domain.com

온라인 도구 사용:
- [SSL Labs Test](https://www.ssllabs.com/ssltest/)에서 A 등급 확인

### 4. Spring Boot 앱 연결 확인

```bash
# Health check
curl https://your-domain.com/actuator/health

# API 테스트
curl https://your-domain.com/api/test
```

---

## 트러블슈팅

### 문제 1: 포트 80/443 연결 불가

**증상:**
```bash
curl: (7) Failed to connect to your-domain.com port 80: Connection refused
```

**해결:**
1. iptables 규칙 확인:
   ```bash
   sudo iptables -L -n -v | grep -E '(80|443)'
   ```

2. OCI Security List 확인 (OCI Console)

3. Nginx 상태 확인:
   ```bash
   sudo systemctl status nginx
   sudo journalctl -u nginx -n 50
   ```

### 문제 2: DNS가 OCI IP를 가리키지 않음

**증상:**
```bash
dig +short your-domain.com
# 결과가 다른 IP 또는 없음
```

**해결:**
1. DNS 설정 재확인 (도메인 관리 콘솔)
2. TTL 대기 (5-10분)
3. DNS 캐시 초기화:
   ```bash
   # macOS
   sudo dscacheutil -flushcache

   # Linux
   sudo systemd-resolve --flush-caches
   ```

### 문제 3: Certbot 인증 실패

**증상:**
```
Error getting validation data
The Certificate Authority failed to verify...
```

**해결:**
1. DNS 전파 확인:
   ```bash
   dig +short your-domain.com
   ```

2. 포트 80 접근 테스트:
   ```bash
   curl http://your-domain.com/.well-known/acme-challenge/test
   ```

3. Nginx 로그 확인:
   ```bash
   sudo tail -f /var/log/nginx/error.log
   ```

4. Let's Encrypt 로그 확인:
   ```bash
   sudo tail -f /var/log/letsencrypt/letsencrypt.log
   ```

### 문제 4: Nginx 설정 오류

**증상:**
```
nginx: [emerg] duplicate value "TLSv1.2"
nginx: configuration file test failed
```

**해결:**
- `/etc/letsencrypt/options-ssl-nginx.conf`가 이미 SSL 설정을 포함하므로 중복 제거:
  ```nginx
  # 제거: ssl_protocols TLSv1.2 TLSv1.3;
  # 제거: ssl_prefer_server_ciphers on;

  # 유지: include /etc/letsencrypt/options-ssl-nginx.conf;
  ```

### 문제 5: 인증서 갱신 실패

**해결:**
```bash
# 수동 갱신 시도
sudo certbot renew --force-renewal

# 로그 확인
sudo cat /var/log/letsencrypt/letsencrypt.log

# Nginx 설정 확인 (ACME challenge 경로)
sudo nginx -t
```

### 문제 6: 502 Bad Gateway

**증상:**
HTTPS는 작동하지만 502 에러 발생

**해결:**
1. Spring Boot 앱 상태 확인:
   ```bash
   docker ps
   docker logs floney-app
   ```

2. 포트 8080 확인:
   ```bash
   sudo netstat -tlnp | grep 8080
   curl http://127.0.0.1:8080/actuator/health
   ```

3. SELinux/AppArmor 확인 (필요시):
   ```bash
   sudo aa-status
   ```

---

## 참고 사항

### 인증서 파일 위치

```
/etc/letsencrypt/
├── live/
│   └── your-domain.com/
│       ├── fullchain.pem  → 전체 인증서 체인
│       ├── privkey.pem    → 개인 키
│       ├── cert.pem       → 인증서만
│       └── chain.pem      → 중간 인증서
├── archive/               → 실제 파일 저장소
└── renewal/               → 갱신 설정
```

### Rate Limits (제한 사항)

Let's Encrypt는 다음과 같은 제한이 있습니다:
- **도메인당 주당 50개** 인증서 발급
- **실패한 검증 시도**: 계정당 시간당 5회
- **중복 인증서**: 주당 5개

테스트 시에는 `--dry-run` 또는 `--staging` 옵션 사용을 권장합니다.

### 보안 권장사항

1. **정기적인 보안 업데이트:**
   ```bash
   sudo apt-get update && sudo apt-get upgrade -y
   ```

2. **UFW 방화벽 설정 (선택사항):**
   ```bash
   sudo ufw allow 22/tcp
   sudo ufw allow 80/tcp
   sudo ufw allow 443/tcp
   sudo ufw enable
   ```

3. **Nginx 보안 헤더 추가:**
   ```nginx
   add_header X-Frame-Options "SAMEORIGIN" always;
   add_header X-Content-Type-Options "nosniff" always;
   add_header X-XSS-Protection "1; mode=block" always;
   add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
   ```

---

## 상용(Production) 환경 적용 체크리스트

- [ ] OCI 인스턴스 생성 및 SSH 키 설정
- [ ] iptables 방화벽 규칙 추가 (포트 80, 443)
- [ ] OCI Security List 설정
- [ ] Nginx, Certbot 설치
- [ ] 상용 도메인 DNS A 레코드 설정 (예: floney.store)
- [ ] DNS 전파 확인 (dig 명령어)
- [ ] Certbot으로 SSL 인증서 발급
- [ ] Nginx 설정 파일 업데이트 (도메인명 변경)
- [ ] HTTPS 연결 테스트
- [ ] HTTP → HTTPS 리다이렉트 확인
- [ ] SSL Labs 테스트 (A 등급)
- [ ] 자동 갱신 타이머 활성화 확인
- [ ] 모니터링 설정 (인증서 만료 알림)

---

## 추가 리소스

- [Let's Encrypt 공식 문서](https://letsencrypt.org/docs/)
- [Certbot 사용 가이드](https://certbot.eff.org/)
- [Nginx 공식 문서](https://nginx.org/en/docs/)
- [OCI 방화벽 설정](https://docs.oracle.com/en-us/iaas/Content/Network/Concepts/securitylists.htm)

---

**마지막 업데이트:** 2026-02-09
**작성자:** Floney DevOps Team
**테스트 환경:** OCI Ubuntu 22.04, floney-dev.store
