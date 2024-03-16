package com.floney.floney.book.repository.category;

import com.floney.floney.book.domain.category.entity.Subcategory;

public interface SubcategoryCustomRepository {

    void inactive(Subcategory subcategory);
}
