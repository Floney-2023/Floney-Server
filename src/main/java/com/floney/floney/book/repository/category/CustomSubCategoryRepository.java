package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.CustomSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomSubCategoryRepository extends JpaRepository<CustomSubCategory, Long> {
}
