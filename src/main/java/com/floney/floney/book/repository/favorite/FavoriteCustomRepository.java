package com.floney.floney.book.repository.favorite;

import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.Book;

public interface FavoriteCustomRepository {

    void inactiveAllByBook(Book book);

    void inactiveAllBySubcategory(Subcategory subcategory);
}
