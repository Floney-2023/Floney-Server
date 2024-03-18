package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryCustomRepository {

}
