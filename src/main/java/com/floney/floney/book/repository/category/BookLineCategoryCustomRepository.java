package com.floney.floney.book.repository.category;

public interface BookLineCategoryCustomRepository {
    void deleteBookLineCategory(String bookKey);

    void deleteBookLineCategoryById(Long id);
}
