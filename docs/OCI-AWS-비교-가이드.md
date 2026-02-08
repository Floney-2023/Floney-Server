# OCI vs AWS 리소스 비교 가이드

## 개요

AWS에서 Oracle Cloud Infrastructure (OCI)로 마이그레이션하면서 사용하는 주요 리소스들을 AWS와 비교하여 설명합니다.

---

## 1. Compute (컴퓨팅)

### AWS EC2 ↔ OCI Compute Instance

**AWS EC2 (Elastic Compute Cloud)**
- 가상 서버 인스턴스
- 용도: 애플리케이션 호스팅
- 인스턴스 타입: t3.medium, t3.large 등
- 비용: 온디맨드, 예약 인스턴스, 스팟 인스턴스

**OCI Compute Instance**
- 동일한 기능: 가상 서버 인스턴스
- 용도: 애플리케이션 호스팅
- Shape: VM.Standard.A1.Flex (ARM), VM.Standard.E4.Flex (AMD)
- 비용: **Free Tier 제공** (Ampere ARM 최대 4 OCPU, 24GB RAM 무료!)

**현재 프로젝트 사용:**
```
이전: AWS EC2 t3.medium (2 vCPU, 4 GB RAM) - $30/월
이후: OCI VM.Standard.A1.Flex (2 OCPU, 12 GB RAM) - $0/월 (Free Tier)
```

**생성 명령어 (OCI CLI):**
```bash
oci compute instance launch \
  --availability-domain "Vfnn:AP-CHUNCHEON-1-AD-1" \
  --compartment-id <compartment-ocid> \
  --shape "VM.Standard.A1.Flex" \
  --shape-config '{"ocpus":2,"memory-in-gbs":12}' \
  --display-name "floney-dev-server" \
  --image-id <ubuntu-image-ocid> \
  --subnet-id <public-subnet-ocid> \
  --assign-public-ip true \
  --ssh-authorized-keys-file ~/.ssh/id_rsa.pub
```

---

## 2. Networking (네트워킹)

### 2-1. VPC/VCN (Virtual Network)

**AWS VPC (Virtual Private Cloud)**
- 격리된 가상 네트워크
- CIDR 블록 정의 (예: 10.0.0.0/16)
- 인터넷 게이트웨이, NAT 게이트웨이 연결

**OCI VCN (Virtual Cloud Network)**
- 동일한 기능: 격리된 가상 네트워크
- CIDR 블록 정의
- Internet Gateway, NAT Gateway 연결

**현재 프로젝트:**
```
VCN Name: vcn-20260207-1328
VCN OCID: ocid1.vcn.oc1.ap-chuncheon-1.amaaaaaa7tt32uqa...
```

**생성 명령어:**
```bash
oci network vcn create \
  --compartment-id <compartment-ocid> \
  --display-name "floney-vcn" \
  --cidr-block "10.0.0.0/16"
```

---

### 2-2. Subnet

**AWS Subnet**
- VPC 내 IP 주소 범위 분할
- Public Subnet: 인터넷 게이트웨이 라우팅
- Private Subnet: NAT 게이트웨이 라우팅

**OCI Subnet**
- 동일한 기능: VCN 내 IP 주소 범위 분할
- Public Subnet: Internet Gateway 라우팅
- Private Subnet: NAT Gateway 라우팅

**차이점:**
- AWS: AZ(Availability Zone)별로 서브넷 생성
- OCI: AD(Availability Domain)별로 서브넷 생성 (선택사항)

---

### 2-3. Security Groups/Lists

**AWS Security Group**
- 인스턴스 레벨 방화벽 (Stateful)
- Inbound/Outbound 규칙
- 동적으로 규칙 추가/제거

**OCI Security List**
- 서브넷 레벨 방화벽 (Stateful)
- Ingress/Egress 규칙
- VCN의 기본 방화벽

**현재 프로젝트 설정:**
```
포트 22 (SSH): 0.0.0.0/0 허용
포트 8080 (애플리케이션): 0.0.0.0/0 허용
포트 443 (HTTPS): 0.0.0.0/0 허용
```

**규칙 추가 명령어:**
```bash
oci network security-list update \
  --security-list-id <security-list-ocid> \
  --ingress-security-rules '[
    {"source":"0.0.0.0/0","protocol":"6","tcp-options":{"destination-port-range":{"min":22,"max":22}}},
    {"source":"0.0.0.0/0","protocol":"6","tcp-options":{"destination-port-range":{"min":8080,"max":8080}}}
  ]'
```

---

### 2-4. Elastic IP / Public IP

**AWS Elastic IP**
- 고정 Public IP 주소
- 인스턴스에 할당/해제 가능
- 비용: 미사용 시 과금

**OCI Public IP**
- **Ephemeral Public IP**: 임시 IP (인스턴스 종료 시 해제)
- **Reserved Public IP**: 고정 IP (인스턴스 독립적)
- 비용: Reserved Public IP만 과금 (~$0.01/시간)

**현재 프로젝트:**
```
IP: 158.179.170.19 (Ephemeral)
비용: $0 (Ephemeral은 무료)
```

---

## 3. Storage (스토리지)

### 3-1. S3 / Object Storage

**AWS S3 (Simple Storage Service)**
- 객체 스토리지
- 버킷 단위 관리
- Pre-signed URL 지원
- 비용: $0.023/GB/월

**OCI Object Storage**
- 동일한 기능: 객체 스토리지
- 버킷 단위 관리
- **S3 호환 API 지원** (기존 AWS SDK 사용 가능!)
- Pre-authenticated Request (PAR) - AWS Pre-signed URL과 동일
- 비용: $0.0255/GB/월 (Standard), $0.01/GB/월 (Archive)

**S3 호환 모드:**
```
엔드포인트: https://<namespace>.compat.objectstorage.<region>.oraclecloud.com
인증: Customer Secret Keys (S3 Access Key/Secret Key와 동일)
```

**현재 프로젝트 마이그레이션:**
```
이전: AWS S3 bucket "floney-images"
이후: OCI Object Storage bucket "floney-images-dev"
     S3 호환 API 사용으로 코드 변경 최소화!
```

**버킷 생성 명령어:**
```bash
oci os bucket create \
  --name "floney-images-dev" \
  --compartment-id <compartment-ocid>
```

**S3 호환 인증 키 생성:**
```bash
oci iam customer-secret-key create \
  --user-id <user-ocid> \
  --display-name "floney-s3-key"
```

---

### 3-2. EBS / Block Volume

**AWS EBS (Elastic Block Store)**
- 인스턴스 연결 디스크
- 용도: Boot Volume, Data Volume
- 타입: gp3, io2 등

**OCI Block Volume**
- 동일한 기능: 인스턴스 연결 디스크
- 용도: Boot Volume, Attached Volume
- 타입: Basic, Balanced, High Performance

**현재 프로젝트:**
```
Boot Volume: 50GB (기본값)
```

---

## 4. Database (데이터베이스)

### AWS RDS MySQL ↔ OCI MySQL Database Service

**AWS RDS MySQL**
- 관리형 MySQL 데이터베이스
- 자동 백업, 패치
- Multi-AZ 고가용성
- 비용: db.t3.medium ~$60/월

**OCI MySQL Database Service**
- 동일한 기능: 관리형 MySQL
- 자동 백업, 패치
- High Availability (HA) 지원
- 비용: 1 OCPU, 15GB ~$30/월 (약 50% 저렴!)

**현재 프로젝트:**
```
이전: AWS RDS MySQL (db.t3.medium) - $60/월
이후: OCI MySQL Database Service (1 OCPU) - $30/월
```

**생성 명령어:**
```bash
oci mysql db-system create \
  --compartment-id <compartment-ocid> \
  --shape-name "MySQL.VM.Standard.E3.1.8GB" \
  --admin-username "admin" \
  --admin-password "SecurePassword123!" \
  --availability-domain <ad-name> \
  --subnet-id <private-subnet-ocid> \
  --data-storage-size-in-gbs 50
```

---

## 5. CI/CD 및 배포

### 5-1. CodeDeploy / Custom Deployment

**AWS CodeDeploy**
- 자동 애플리케이션 배포
- S3에서 artifact 다운로드
- EC2에 배포
- appspec.yml로 배포 스크립트 정의

**OCI 배포 방식 (Custom)**
- OCI Object Storage에 artifact 업로드
- SSH로 Compute Instance 접속
- OCI CLI로 artifact 다운로드
- 배포 스크립트 실행

**현재 프로젝트 마이그레이션:**
```yaml
# AWS CodeDeploy (기존)
GitHub Actions → Build → S3 Upload → CodeDeploy → EC2

# OCI Custom Deployment (신규)
GitHub Actions → Build → OCI Object Storage Upload → SSH → OCI Compute
```

**GitHub Actions Workflow 차이:**

**AWS (기존):**
```yaml
- name: AWS 인증
  uses: aws-actions/configure-aws-credentials@v1

- name: S3에 업로드
  run: aws deploy push --application-name floney-dev

- name: EC2에 배포
  run: aws deploy create-deployment
```

**OCI (신규):**
```yaml
- name: OCI CLI 설치
  run: bash install-oci-cli.sh

- name: OCI Object Storage에 업로드
  run: oci os object put --bucket-name floney-build-artifacts

- name: SSH로 배포
  uses: appleboy/ssh-action@master
  with:
    host: ${{ secrets.OCI_DEV_HOST }}
    script: |
      oci os object get --bucket-name floney-build-artifacts
      ./script/start.sh
```

---

## 6. Identity & Access Management

### AWS IAM ↔ OCI IAM

**AWS IAM**
- 사용자, 그룹, 역할 관리
- 정책(Policy)으로 권한 제어
- Access Key / Secret Key

**OCI IAM**
- 동일한 기능: 사용자, 그룹, 정책
- Compartment로 리소스 격리
- **API Keys**: OCI CLI/SDK 인증
- **Customer Secret Keys**: S3 호환 API 인증

**현재 프로젝트 인증:**
```
1. API Key: OCI CLI로 리소스 관리
   - User OCID, Tenancy OCID, Fingerprint, Private Key

2. Customer Secret Key: S3 호환 API (Object Storage)
   - Access Key, Secret Key (AWS와 동일)
```

---

## 7. 주요 용어 비교표

| AWS 용어 | OCI 용어 | 설명 |
|---------|---------|------|
| Region | Region | 지리적 위치 (ap-northeast-2 ↔ ap-chuncheon-1) |
| Availability Zone (AZ) | Availability Domain (AD) | 데이터센터 그룹 |
| VPC | VCN | 가상 네트워크 |
| Subnet | Subnet | 네트워크 세그먼트 |
| Security Group | Security List | 방화벽 규칙 |
| EC2 Instance | Compute Instance | 가상 서버 |
| Elastic IP | Reserved Public IP | 고정 Public IP |
| S3 Bucket | Object Storage Bucket | 객체 스토리지 |
| RDS | Database Service | 관리형 데이터베이스 |
| IAM Role | Dynamic Group | 동적 권한 할당 |
| Access Key | Customer Secret Key | S3 호환 인증 |
| API Key | API Signing Key | API 인증 |

---

## 8. 비용 비교 (월 기준)

| 항목 | AWS | OCI | 절감 |
|-----|-----|-----|-----|
| Compute (Dev) | EC2 t3.medium: $30 | VM.Standard.A1.Flex (Free): $0 | $30 |
| Compute (Prod) | EC2 t3.large: $60 | VM.Standard.E4.Flex: $45 | $15 |
| Database | RDS MySQL: $60 | MySQL Database: $30 | $30 |
| Storage | S3 50GB: $15 | Object Storage 50GB: $8 | $7 |
| Data Transfer | $9 | $0 (10TB 무료) | $9 |
| **총합** | **$174** | **$83** | **$91 (52%)** |

**연간 절감액: ~$1,092**

---

## 9. OCI 고유 개념

### 9-1. Namespace

**설명:**
- Object Storage의 전역 고유 식별자
- 계정당 하나
- 버킷 URL에 사용: `https://objectstorage.ap-chuncheon-1.oraclecloud.com/n/<namespace>/b/<bucket>/o/<object>`

**현재 프로젝트:**
```
Namespace: axxrjjk2vpny
```

**확인 명령어:**
```bash
oci os ns get
```

---

### 9-2. Compartment

**설명:**
- 리소스 격리 및 조직화
- AWS의 태그와 유사하지만 더 강력
- 정책(Policy)을 Compartment 단위로 적용

**현재 프로젝트:**
```
Compartment: sienna011022 (root)
```

**생성 명령어:**
```bash
oci iam compartment create \
  --name "floney-compartment" \
  --description "Floney application resources"
```

---

### 9-3. OCID (Oracle Cloud Identifier)

**설명:**
- 모든 OCI 리소스의 고유 식별자
- 형식: `ocid1.<resource-type>.<realm>.<region>.<unique-id>`
- 예시: `ocid1.instance.oc1.ap-chuncheon-1.amaaaaaa...`

**리소스 타입:**
- `ocid1.user.*` - 사용자
- `ocid1.tenancy.*` - Tenancy (계정)
- `ocid1.instance.*` - Compute Instance
- `ocid1.vcn.*` - VCN
- `ocid1.subnet.*` - Subnet

---

## 10. 마이그레이션 체크리스트

### ✅ 완료된 작업

- [x] OCI CLI 설치 및 인증 설정
- [x] Namespace 확인: `axxrjjk2vpny`
- [x] VCN 확인: `vcn-20260207-1328`
- [x] Availability Domain 확인: `Vfnn:AP-CHUNCHEON-1-AD-1`

### 🔄 진행 중

- [ ] Compute Instance 생성 (floney-dev-server)
- [ ] Security List 규칙 추가 (포트 22, 8080, 443)
- [ ] Object Storage 버킷 생성 (floney-images-dev, floney-build-artifacts)
- [ ] Customer Secret Keys 생성 (S3 호환)

### ⏳ 대기 중

- [ ] MySQL Database Service 생성 (프로덕션)
- [ ] 설정 파일 업데이트 (application-*.yaml)
- [ ] GitHub Actions Workflow 테스트
- [ ] CI/CD 파이프라인 검증

---

## 11. 참고 링크

**OCI 공식 문서:**
- [OCI vs AWS 비교](https://docs.oracle.com/en-us/iaas/Content/General/Reference/oci-aws.htm)
- [OCI CLI 참조](https://docs.oracle.com/en-us/iaas/tools/oci-cli/latest/oci_cli_docs/)
- [S3 호환 API](https://docs.oracle.com/en-us/iaas/Content/Object/Tasks/s3compatibleapi.htm)

**AWS to OCI 마이그레이션 가이드:**
- [Compute 마이그레이션](https://docs.oracle.com/en-us/iaas/Content/Resources/Assets/whitepapers/aws-to-oci-migration-guide.pdf)
- [네트워킹 비교](https://docs.oracle.com/en-us/iaas/Content/Network/Concepts/overview.htm)

---

## 12. 작업 로그

### 2026-02-07

**14:00 - OCI CLI 설정 완료**
```bash
# Config 파일 생성
~/.oci/config

# Namespace 확인
axxrjjk2vpny

# 기존 VCN 확인
vcn-20260207-1328
```

**14:30 - 다음 단계**
- Compute Instance 생성
- Public Subnet 확인 또는 생성
- Security List 규칙 추가

---

이 문서는 OCI 마이그레이션 작업을 진행하면서 지속적으로 업데이트됩니다.
