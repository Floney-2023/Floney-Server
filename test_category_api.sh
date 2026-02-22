#!/bin/bash

echo "=== 카테고리 API categoryKey 필드 검증 스크립트 ==="
echo ""
echo "이 스크립트는 다음을 검증합니다:"
echo "1. CategoryInfo DTO에 categoryKey 필드가 있는지"
echo "2. 엔티티에 categoryKey 필드가 있는지"
echo "3. 쿼리에서 categoryKey를 프로젝션하는지"
echo ""

echo "✅ 1. CategoryInfo DTO 검증"
grep -A 3 "private String categoryKey" src/main/java/com/floney/floney/book/dto/process/CategoryInfo.java && echo "   - categoryKey 필드 존재" || echo "   ❌ categoryKey 필드 없음"
grep "categoryKey" src/main/java/com/floney/floney/book/dto/process/CategoryInfo.java | grep -q "QueryProjection" && echo "   - QueryProjection 생성자에 포함" || echo "   ⚠️  QueryProjection 확인 필요"

echo ""
echo "✅ 2. Entity 클래스 검증"
grep -q "private String categoryKey" src/main/java/com/floney/floney/book/domain/category/entity/DefaultSubcategory.java && echo "   - DefaultSubcategory에 categoryKey 필드 존재" || echo "   ❌ DefaultSubcategory에 categoryKey 필드 없음"
grep -q "private String categoryKey" src/main/java/com/floney/floney/book/domain/category/entity/Subcategory.java && echo "   - Subcategory에 categoryKey 필드 존재" || echo "   ❌ Subcategory에 categoryKey 필드 없음"
grep -q ".categoryKey(defaultSubcategory.getCategoryKey())" src/main/java/com/floney/floney/book/domain/category/entity/Subcategory.java && echo "   - Subcategory.of()에서 categoryKey 복사" || echo "   ❌ Subcategory.of()에서 categoryKey 미복사"

echo ""
echo "✅ 3. Repository 쿼리 검증"
grep -q "subcategory.categoryKey" src/main/java/com/floney/floney/book/repository/category/CategoryRepositoryImpl.java && echo "   - 쿼리에서 categoryKey 프로젝션" || echo "   ❌ 쿼리에서 categoryKey 미프로젝션"

echo ""
echo "✅ 4. 마이그레이션 파일 검증"
if [ -f "src/main/resources/db/migration/mysql/V29__add_category_key_for_i18n.sql" ]; then
    echo "   - V29 마이그레이션 파일 존재"
    grep -q "ADD COLUMN \`category_key\`" src/main/resources/db/migration/mysql/V29__add_category_key_for_i18n.sql && echo "   - category_key 컬럼 추가" || echo "   ❌ category_key 컬럼 추가 안됨"
    grep -q "SALARY" src/main/resources/db/migration/mysql/V29__add_category_key_for_i18n.sql && echo "   - 기본 카테고리 키 값 설정" || echo "   ❌ 기본 카테고리 키 값 미설정"
else
    echo "   ❌ V29 마이그레이션 파일 없음"
fi

echo ""
echo "✅ 5. 컴파일 검증"
./gradlew compileJava compileTestJava --quiet && echo "   - 컴파일 성공 ✅" || echo "   ❌ 컴파일 실패"

echo ""
echo "=== 검증 완료 ==="
echo ""
echo "📝 다음 단계:"
echo "1. 실제 MySQL 데이터베이스에서 V29 마이그레이션 실행"
echo "2. 애플리케이션 시작 후 카테고리 API 호출:"
echo "   curl -H 'Authorization: Bearer <token>' http://localhost:8080/books/{bookKey}/categories?parent=수입"
echo "3. 응답에 categoryKey 필드 확인"
