package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String parent);

    Optional<Category> findByNameAndBook(String name, Book savedBook);
}
