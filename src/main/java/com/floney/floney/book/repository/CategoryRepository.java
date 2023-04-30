package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Category;
import com.floney.floney.book.entity.DefaultCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryCustomRepository {

    Optional<Category> findByName(String parent);

}
