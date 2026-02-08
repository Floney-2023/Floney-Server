# OCI Docker 배포 가이드 (E2.1.Micro 1GB 인스턴스)

## 📋 현재 상태

### ✅ 완료된 작업

1. **OCI 인스턴스 생성**
   - Shape: VM.Standard.E2.1.Micro (1 OCPU, 1GB RAM, Free Tier)
   - OS: Ubuntu 22.04 Minimal (x86)
   - Public IP: `129.154.59.187`
   - SSH Key: floney-oci-new

2. **Docker 설치**
   - Docker 29.2.1 설치 완료
   - 현재 가용 메모리: 608MB

3. **Secrets 설정**
   - `/home/ubuntu/secrets/` 디렉토리 생성
   - 모든 credential 파일 복사 완료:
     - application-dev.yaml
     - AppleRootCA-G2.cer
     - AppleRootCA-G3.cer
     - AuthKey_V577DGHS8Q.p8
     - floney-android.json
     - google-credential.json

4. **코드 변경**
   - Dockerfile 생성 (multi-stage build, JVM 메모리 최적화)
   - .dockerignore 생성
   - GitHub Actions 워크플로우 수정 (Docker 배포 방식)
   - Git 커밋 완료 (develop 브랜치)

---

## 🔑 GitHub Secrets 설정 (필수)

**GitHub 저장소 → Settings → Secrets and variables → Actions**

### 1. OCI_DEV_HOST
```
129.154.59.187
```

### 2. OCI_DEV_SSH_KEY
```
-----BEGIN OPENSSH PRIVATE KEY-----
b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAABFwAAAAdzc2gtcn
NhAAAAAwEAAQAAAQEA1HERMDCeYwbCCfI5kb6Aiw+h4fOqkzdbrRFcyYD0KW+Enmv4LuC3
rsGY2wyZPJWzns8GfHllkPpO3SZPRaonalmH5PKl+hM5CMPQNjdNOozKN89Gzvb2okhuRL
Gxy1S4oU3ADnufCaVHArAZ43pEgSLmDRBdOmp02Sgh9FsUDJcrBDDPhq5GZ3KCBDlZOi4M
zAb1rCKpUApV3kVIMqGhY/R1UXhWaWHeUpPcSSn5F0mG0VCIEka3bVaHZxAvRx2FJSJuQg
rhnWszutzxGKleFiUpdTS2qZJ+I0aBXSxXxsh0S2WAo/+Vnv5hcYrI1d2vkHaQk2zFb1aX
9DKY1M/XLwAAA8BdQnz/XUJ8/wAAAAdzc2gtcnNhAAABAQDUcREwMJ5jBsIJ8jmRvoCLD6
Hh86qTN1utEVzJgPQpb4Sea/gu4LeuwZjbDJk8lbOezwZ8eWWQ+k7dJk9FqidqWYfk8qX6
EzkIw9A2N006jMo3z0bO9vaiSG5EsbHLVLihTcAOe58JpUcCsBnjekSBIuYNEF06anTZKC
H0WxQMlysEMM+GrkZncoIEOVk6LgzMBvWsIqlQClXeRUgyoaFj9HVReFZpYd5Sk9xJKfkX
SYbRUIgSRrdtVodnEC9HHYUlIm5CCuGdazO63PEYqV4WJSl1NLapkn4jRoFdLFfGyHRLZY
Cj/5We/mFxisjV3a+QdpCTbMVvVpf0MpjUz9cvAAAAAwEAAQAAAQEAmZaJG7L6v5NCM9io
oGL/IgqSOf5lQvvRzsTvtUsCuvZCBhr/Gj0i7zhOH4fzPWbprpX0FYfa0z70R1SMGUyUJI
JIClEXiD3fjSOsY5YFwp/JDyJPChBobRk6h3WEmgFNXmkMwivD5kBUn2/bt6dM4aPueGFt
VW9jvSATtEsoqpRKasz81WbVfPaPk9ky2qrgUkIludHdzBBGzkM52asMHwHLnKQzYyXfZK
w7mTqNWcXZ1zMHubWFNH2nFiRR2xFMjdd1FAHMlEK+7uiBT2kWyi3ZQDg6vcDHEWJ9A/XC
/cMtBrDKx9yWkUazx3Bz1laWdmEDUECsvmHgQojkn2pxWQAAAIA93kTzoYeRVyKkZajoi0
+QMtbxWW+DjUlrfUPwC+OJRLjF4Q3vhNrDaIk2r8ifha3/ZvNTPwaLSnZEhhXm0wPP/z30
naFHg4mGNJm6ovCKhND/inTj+mW2vCkHaYAgGdEhk5CZcUIfZ+YQU++M+qNLdQTTCGYfgL
o2g6sm02kufwAAAIEA8v5QJENfB5EKgX2IqSUn7DR/QX6CW/bQMGnxassjD+8R4sS52sZM
njQEK723LOOFwmDKAraAMsWGRBafF+JMOBIeIhHC5lX7xaxhJhHWwh/GvBTKc12YluLlC9
lZZ1Nk3ql5AW6trH25BwyeQyN1h8+ECe/P2LPwKAuMSpVtFg0AAACBAN/QHC4CHPy8+rvs
AjXE/vqsQOXAQrq/wnhox/xZDuDZHeIjn8cKILfXezdWJZzPhujxJ9bIg937wWVp6i2haV
gDvg8VXTItv2NT/Eqq9WSJb3HE1paJH+i4YOfgxe3X+daDODDGUCnFtcXyHpiJz87FjxiU
EC0ler+9zu94w+8rAAAACmZsb25leS1vY2k=
-----END OPENSSH PRIVATE KEY-----
```

---

## 🚀 다음 단계

### 1. GitHub Secrets 등록
위의 2개 Secret을 GitHub에 등록하세요.

### 2. 코드 푸시 및 배포 테스트
```bash
# develop 브랜치에 푸시
git push origin develop

# GitHub Actions에서 배포 진행
# - Docker 이미지 빌드
# - GitHub Container Registry에 푸시
# - OCI 인스턴스에서 컨테이너 실행
```

### 3. 배포 모니터링
```bash
# SSH 접속
ssh -i ~/Downloads/floney-oci-new ubuntu@129.154.59.187

# 컨테이너 상태 확인
docker ps

# 로그 확인
docker logs -f floney-app

# 메모리 사용량 확인
docker stats floney-app

# 애플리케이션 헬스체크
curl http://localhost:8080/actuator/health
```

---

## 📊 메모리 최적화 설정

### JVM 설정 (Dockerfile 내)
```bash
-Xms256m              # 초기 힙 크기: 256MB
-Xmx512m              # 최대 힙 크기: 512MB
-XX:MaxMetaspaceSize=128m  # 메타스페이스: 128MB
-XX:+UseSerialGC      # Serial GC 사용 (메모리 효율적)
-XX:MaxRAMPercentage=75.0  # 최대 RAM 사용률 75%
```

### 예상 메모리 사용량
- JVM 힙: ~512MB
- Metaspace: ~128MB
- Docker 오버헤드: ~50MB
- OS 버퍼: ~100MB
- **총 예상: ~800MB / 1GB**

---

## 🔍 트러블슈팅

### OOM (Out of Memory) 발생 시
```bash
# 더 작은 힙 크기로 재시작
docker stop floney-app
docker rm floney-app
docker run -d \
  --name floney-app \
  --restart unless-stopped \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e JAVA_OPTS="-Xms128m -Xmx384m -XX:MaxMetaspaceSize=96m" \
  -v /home/ubuntu/secrets:/app/secrets:ro \
  ghcr.io/YOUR_REPO:develop
```

### 컨테이너 재시작
```bash
docker restart floney-app
```

### 이미지 업데이트
```bash
docker pull ghcr.io/YOUR_REPO:develop
docker stop floney-app
docker rm floney-app
# ... docker run 명령어 다시 실행
```

---

## 💰 비용 절감 효과

### 현재 (AWS)
- EC2 t3.medium: ~$30/월
- S3: ~$2.30/월
- RDS MySQL: ~$15/월
- CodeDeploy: 무료
- **총: ~$47/월**

### 마이그레이션 후 (OCI)
- Compute E2.1.Micro: **$0** (Free Tier)
- Object Storage: ~$0.20/월
- MySQL (로컬 또는 AWS 유지): $0 or $15/월
- **총: ~$0.20 - $15.20/월**

### 절감액
- **$32 - $47/월** (68-100% 절감)
- **연간 $384 - $564 절감**

---

## ⚠️ 주의사항

1. **메모리 제약**
   - 1GB는 Spring Boot에 최소 사양입니다
   - 트래픽 급증 시 OOM 발생 가능
   - 프로덕션 환경에는 최소 2GB 이상 권장

2. **CPU 성능**
   - 1 OCPU (2 vCPU)로 동시 요청 처리 제한적
   - API 응답 시간이 AWS 대비 느릴 수 있음

3. **ARM vs x86**
   - 현재는 x86 (E2.1.Micro) 사용 중
   - ARM (A1.Flex)이 가용되면 2 OCPU + 12GB로 업그레이드 권장

4. **데이터베이스**
   - 현재 127.0.0.1:3306 (로컬 MySQL)로 설정되어 있음
   - 실제 사용 전 DB 연결 정보 확인 필요

---

## 📝 체크리스트

### 배포 전
- [ ] GitHub Secrets 등록 (OCI_DEV_HOST, OCI_DEV_SSH_KEY)
- [ ] DB 연결 정보 확인 (application-dev.yaml)
- [ ] OCI Object Storage 버킷 생성 및 접근 테스트

### 배포 후
- [ ] GitHub Actions 워크플로우 성공 확인
- [ ] 컨테이너 실행 확인 (docker ps)
- [ ] Health check 성공 확인
- [ ] API 테스트 (Pre-signed URL 생성)
- [ ] 메모리 사용량 모니터링 (docker stats)
- [ ] 1-2일간 안정성 모니터링

### 프로덕션 마이그레이션 전
- [ ] 개발 환경 7일 이상 안정적 운영
- [ ] 성능 벤치마크 완료
- [ ] ARM A1.Flex 인스턴스 확보 (2 OCPU, 12GB)
- [ ] 프로덕션 DB 마이그레이션 계획 수립
