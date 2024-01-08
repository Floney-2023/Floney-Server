package com.floney.floney.category.repository;

import com.floney.floney.category.domain.entity.CustomCategoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomCategoryDetailRepository extends JpaRepository<CustomCategoryDetail, Long> {
}
