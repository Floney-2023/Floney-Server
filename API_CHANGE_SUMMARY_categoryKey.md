# 카테고리 API 변경 요약 (TL;DR)

## 🎯 핵심 변경사항

카테고리 조회 API에 **`categoryKey`** 필드가 추가되었습니다. (하위 호환 ✅)

## 📊 Before & After

### Before
```json
{
  "isDefault": true,
  "name": "급여"
}
```

### After
```json
{
  "isDefault": true,
  "name": "급여",
  "categoryKey": "SALARY"  // ← NEW!
}
```

## 🔑 categoryKey란?

- **기본 카테고리**: 영문 키 값 (예: `SALARY`, `FOOD`, `TRANSPORTATION`)
- **사용자 정의 카테고리**: `null`
- **용도**: 클라이언트 다국어 처리용 식별자

## 💻 클라이언트 구현 (3줄 요약)

```kotlin
fun getDisplayName(category: CategoryInfo): String {
    return when {
        category.categoryKey != null -> i18n.translate(category.categoryKey) // 다국어 변환
        else -> category.name // 사용자 정의는 그대로
    }
}
```

## ✅ 체크리스트

- [ ] `categoryKey` 필드 파싱 추가
- [ ] 다국어 리소스 파일에 35개 카테고리 키 추가 (상세 명세서 참고)
- [ ] `categoryKey == null` 처리 로직 추가
- [ ] 기존 `name` 필드는 fallback으로 사용

## 📝 전체 상세 명세

→ [API_CHANGE_SPEC_categoryKey.md](./API_CHANGE_SPEC_categoryKey.md)

## 🚨 주의사항

1. **`categoryKey`가 `null`일 수 있음** → null 체크 필수!
2. **기존 `name` 필드 유지** → 삭제하지 마세요!
3. **하위 호환** → 기존 앱도 정상 작동합니다

## 📱 예시: 영어 사용자

| categoryKey | 한글 (name) | 영어 표시 (Figma) |
|-------------|-------------|-------------------|
| `SALARY` | 급여 | Salary |
| `SIDE_INCOME` | 부수입 | Extra Income |
| `FOOD` | 식비 | Food |
| `CAFE_SNACK` | 카페/간식 | Cafe/Snacks |
| `TRANSPORTATION` | 교통 | Transport |
| `CHILDCARE` | 육아 | Family |
| `OCCASIONS` | 경조사 | Events |
| `null` | 주식배당 | 주식배당 (그대로) |

---

**작성**: 2026-01-23 | **배포 예정**: TBD
