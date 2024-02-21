package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.entity.DefaultSubcategory;
import com.floney.floney.common.constant.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DefaultSubcategoryRepository extends JpaRepository<DefaultSubcategory, Long> {

    List<DefaultSubcategory> findAllByStatus(final Status status);
}
