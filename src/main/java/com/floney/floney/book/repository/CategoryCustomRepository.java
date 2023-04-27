package com.floney.floney.book.repository;

import com.floney.floney.book.entity.Category;

public interface CategoryCustomRepository {
    Category findRoot(String root);
}
