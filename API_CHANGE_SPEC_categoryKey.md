# 카테고리 API 변경 명세서 (v2.x - i18n 지원)

## 📋 개요

클라이언트에서 다국어 지원을 위해 카테고리 API 응답에 `categoryKey` 필드가 추가되었습니다.

### 변경 사항
- **변경 타입**: 하위 호환 추가 (Breaking Change 없음)
- **영향 범위**: `GET /books/{bookKey}/categories` API 응답 필드 추가
- **배포 예정일**: TBD

---

## 🔄 API 변경 내역

### Endpoint
```
GET /books/{bookKey}/categories
```

### Request (변경 없음)
```
GET /books/{bookKey}/categories?parent=수입
Authorization: Bearer {accessToken}
```

### Response (변경 후)

#### 1. 기본 카테고리 (Default Categories)
기본 카테고리는 이제 `categoryKey` 필드를 포함합니다.

```json
[
  {
    "isDefault": true,
    "name": "급여",
    "categoryKey": "Salary"
  },
  {
    "isDefault": true,
    "name": "부수입",
    "categoryKey": "Extra Income"
  },
  {
    "isDefault": true,
    "name": "용돈",
    "categoryKey": "Allowance"
  }
]
```

#### 2. 사용자 정의 카테고리 (Custom Categories)
사용자가 직접 추가한 카테고리는 `categoryKey`가 `null`입니다.

```json
[
  {
    "isDefault": false,
    "name": "주식 배당",
    "categoryKey": null
  },
  {
    "isDefault": false,
    "name": "프리랜서 수입",
    "categoryKey": null
  }
]
```

---

## 📝 필드 명세

### CategoryInfo Object

| 필드 | 타입 | 필수 | 설명 | 비고 |
|------|------|------|------|------|
| `isDefault` | `boolean` | O | 기본 카테고리 여부 | 기존 필드 (유지) |
| `name` | `String` | O | 카테고리 이름 (한국어) | 기존 필드 (유지) |
| **`categoryKey`** | **`String`** | **O** | **카테고리 식별 키 (영문)** | **✨ 신규 추가** |

#### categoryKey 값 설명
- **기본 카테고리**: Figma 영어 이름 그대로 (예: `Salary`, `Extra Income`, `Food`, `Cafe/Snacks`)
- **사용자 정의 카테고리**: `null`
- **다국어 처리**: 클라이언트에서 `categoryKey`를 기준으로 다국어 매핑
- **특징**: 공백과 슬래시(/) 포함 가능

---

## 🗂️ Category Key 목록

### 수입 (INCOME) - 8개
| Korean Name | Category Key (Figma English) |
|-------------|-------------------------------|
| 급여 | `Salary` |
| 부수입 | `Extra Income` |
| 용돈 | `Allowance` |
| 금융소득 | `Financial Income` |
| 사업소득 | `Business Income` |
| 상여금 | `Bonus` |
| 기타 | `Other` |
| 미분류 | `Uncategorized` |

### 지출 (OUTCOME) - 14개
| Korean Name | Category Key (Figma English) |
|-------------|-------------------------------|
| 식비 | `Food` |
| 카페/간식 | `Cafe/Snacks` |
| 교통 | `Transport` |
| 주거/통신 | `Housing/Phone` |
| 의료/건강 | `Health` |
| 문화 | `Culture` |
| 여행/숙박 | `Travel/Stay` |
| 생활 | `Living` |
| 패션/미용 | `Style/Beauty` |
| 육아 | `Family` |
| 교육 | `Education` |
| 경조사 | `Events` |
| 기타 | `Other` |
| 미분류 | `Uncategorized` |

### 이체 (TRANSFER) - 9개
| Korean Name | Category Key (Figma English) |
|-------------|-------------------------------|
| 이체 | `Transfer` |
| 저축 | `Savings` |
| 현금 | `Cash` |
| 투자 | `Investment` |
| 보험 | `Insurance` |
| 카드대금 | `Card Payment` |
| 대출 | `Loan` |
| 기타 | `Other` |
| 미분류 | `Uncategorized` |

### 자산 (ASSET) - 4개
| Korean Name | Category Key (Figma English) |
|-------------|-------------------------------|
| 현금 | `Cash` |
| 체크카드 | `Debit Card` |
| 신용카드 | `Credit Card` |
| 은행 | `Bank` |

---

## 💡 클라이언트 구현 가이드

### 1. 기본 처리 로직
```kotlin
// Kotlin 예시
data class CategoryInfo(
    val isDefault: Boolean,
    val name: String,
    val categoryKey: String? // nullable
)

fun getCategoryDisplayName(category: CategoryInfo, locale: Locale): String {
    return when {
        // 1. categoryKey가 있으면 다국어 변환
        category.categoryKey != null -> {
            when (locale) {
                Locale.ENGLISH -> translateCategoryKey(category.categoryKey)
                Locale.KOREAN -> category.name // 한국어는 서버에서 받은 name 사용
                else -> category.name
            }
        }
        // 2. categoryKey가 null이면 사용자 정의 카테고리 → name 그대로 표시
        else -> category.name
    }
}

fun getCategoryDisplayName(category: CategoryInfo, locale: Locale): String {
    return when {
        // categoryKey가 null이면 사용자 정의 카테고리
        category.categoryKey == null -> category.name

        // 영어 로케일이면 categoryKey 그대로 사용 (이미 영어)
        locale == Locale.ENGLISH -> category.categoryKey

        // 한국어 로케일이면 서버에서 받은 name 사용
        locale == Locale.KOREAN -> category.name

        // 다른 언어는 다국어 리소스에서 변환
        else -> translateCategoryKey(category.categoryKey, locale)
    }
}

fun translateCategoryKey(key: String, locale: Locale): String {
    // 다국어 리소스에서 변환 (예: 일본어)
    return when (locale) {
        Locale.JAPANESE -> when (key) {
            "Salary" -> "給与"
            "Extra Income" -> "副収入"
            "Food" -> "食費"
            // ...
            else -> key
        }
        else -> key
    }
}
```

### 2. Swift 예시
```swift
struct CategoryInfo: Codable {
    let isDefault: Bool
    let name: String
    let categoryKey: String?
}

func getCategoryDisplayName(_ category: CategoryInfo) -> String {
    guard let key = category.categoryKey else {
        // 사용자 정의 카테고리
        return category.name
    }

    // 다국어 처리
    return NSLocalizedString("category.\(key)", value: category.name, comment: "")
}
```

### 3. 추천 다국어 파일 구조
```
// en.json
{
  "category.SALARY": "Salary",
  "category.SIDE_INCOME": "Extra Income",
  "category.FOOD": "Food",
  "category.CAFE_SNACK": "Cafe/Snacks",
  "category.TRANSPORTATION": "Transport",
  "category.HOUSING_COMMUNICATION": "Housing/Phone",
  "category.MEDICAL_HEALTH": "Health",
  "category.CHILDCARE": "Family",
  "category.OCCASIONS": "Events",
  ...
}

// ko.json
{
  "category.SALARY": "급여",
  "category.SIDE_INCOME": "부수입",
  "category.FOOD": "식비",
  "category.CAFE_SNACK": "카페/간식",
  "category.TRANSPORTATION": "교통",
  "category.HOUSING_COMMUNICATION": "주거/통신",
  "category.MEDICAL_HEALTH": "의료/건강",
  "category.CHILDCARE": "육아",
  "category.OCCASIONS": "경조사",
  ...
}

// ja.json (예시)
{
  "category.SALARY": "給与",
  "category.SIDE_INCOME": "副収入",
  "category.FOOD": "食費",
  "category.CAFE_SNACK": "カフェ・軽食",
  "category.TRANSPORTATION": "交通費",
  "category.HOUSING_COMMUNICATION": "住居・通信",
  "category.MEDICAL_HEALTH": "医療・健康",
  "category.CHILDCARE": "育児",
  "category.OCCASIONS": "冠婚葬祭",
  ...
}
```

---

## ⚠️ 주의사항

### 1. 하위 호환성
- ✅ 기존 필드 (`isDefault`, `name`)는 **변경 없음**
- ✅ `categoryKey`는 **추가 필드**이므로 기존 클라이언트는 무시 가능
- ✅ 새 필드를 사용하지 않는 클라이언트는 **영향 없음**

### 2. 사용자 정의 카테고리 처리
```javascript
// ❌ 잘못된 처리
if (category.categoryKey === null) {
    throw new Error("Invalid category");
}

// ✅ 올바른 처리
const displayName = category.categoryKey
    ? translate(category.categoryKey)
    : category.name; // null이면 name을 그대로 사용
```

### 3. Fallback 처리
서버에서 받은 `name` 필드는 항상 유효한 한국어 이름이므로, 다국어 변환에 실패하거나 `categoryKey`가 `null`인 경우 **`name`을 fallback으로 사용**하세요.

```typescript
function getCategoryName(category: CategoryInfo, locale: string): string {
    if (!category.categoryKey) {
        return category.name; // 사용자 정의 카테고리
    }

    const translated = i18n.translate(`category.${category.categoryKey}`, locale);
    return translated || category.name; // fallback to Korean name
}
```

---

## 🧪 테스트 시나리오

### 시나리오 1: 기본 카테고리 조회
```
Request: GET /books/{bookKey}/categories?parent=수입

Expected Response:
[
  {
    "isDefault": true,
    "name": "급여",
    "categoryKey": "SALARY"
  },
  ...
]
```

### 시나리오 2: 사용자 정의 카테고리 조회
```
1. 사용자가 "해외송금" 카테고리 추가
2. GET /books/{bookKey}/categories?parent=수입

Expected Response:
[
  {
    "isDefault": true,
    "name": "급여",
    "categoryKey": "SALARY"
  },
  {
    "isDefault": false,
    "name": "해외송금",
    "categoryKey": null  // ← 사용자 정의는 null
  },
  ...
]
```

### 시나리오 3: 다국어 표시 (클라이언트)
```
영어 사용자:
- categoryKey="SALARY" → "Salary" 표시
- categoryKey=null, name="해외송금" → "해외송금" 그대로 표시

일본어 사용자:
- categoryKey="SALARY" → "給与" 표시
- categoryKey=null, name="해외송금" → "해외송금" 그대로 표시
```

---

## 📅 마이그레이션 일정

| 단계 | 작업 | 담당 | 상태 |
|------|------|------|------|
| 1 | 서버 API 개발 | 백엔드 | ✅ 완료 |
| 2 | API 배포 | DevOps | 🔄 예정 |
| 3 | 네이티브 앱 업데이트 | iOS/Android | 📋 대기 |
| 4 | 다국어 리소스 추가 | iOS/Android | 📋 대기 |
| 5 | QA 테스트 | QA | 📋 대기 |

---

## 🔗 관련 문서

- [DB 마이그레이션 V29](./src/main/resources/db/migration/mysql/V29__add_category_key_for_i18n.sql)
- [CategoryInfo DTO](./src/main/java/com/floney/floney/book/dto/process/CategoryInfo.java)
- [Category API Controller](./src/main/java/com/floney/floney/book/controller/CategoryController.java)

---

## ❓ Q&A

### Q1: 기존 앱 버전도 정상 동작하나요?
**A**: 네, 하위 호환됩니다. `categoryKey` 필드를 무시하고 기존처럼 `name` 필드만 사용하면 됩니다.

### Q2: 사용자 정의 카테고리는 어떻게 처리하나요?
**A**: `categoryKey`가 `null`인 경우 `name` 필드를 그대로 표시하면 됩니다.

### Q3: 새로운 기본 카테고리가 추가되면?
**A**: 서버에서 새 `categoryKey`와 함께 제공되며, 클라이언트는 다국어 리소스만 업데이트하면 됩니다.

### Q4: 언어별 번역은 어디서 관리하나요?
**A**: 클라이언트 앱의 다국어 리소스 파일에서 관리합니다. `categoryKey`를 키로 사용하세요.

