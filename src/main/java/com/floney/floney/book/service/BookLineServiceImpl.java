package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookLineResponse;
import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.entity.Category;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.common.exception.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookLineServiceImpl implements BookLineService {
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BookLineResponse addBookLine(CreateLineRequest request) {
        BookLine bookLine = request.to(findBookUser(request), findBook(request));
        bookLine.add(findCategories(request));
        BookLine newBookLine = bookLineRepository.save(bookLine);
        return BookLineResponse.of(newBookLine);
    }

    private Map<CategoryEnum, Category> findCategories(CreateLineRequest request) {
        Map<CategoryEnum, Category> categories = new EnumMap<>(CategoryEnum.class);
        categories.put(CategoryEnum.ASSET,findCategory(request.getBookKey(),request.getAsset()));
        categories.put(CategoryEnum.FLOW,findCategory(request.getBookKey(),request.getFlow()));
        categories.put(CategoryEnum.FLOW_LINE,findCategory(request.getBookKey(),request.getLine()));
        return categories;
    }

    private BookUser findBookUser(CreateLineRequest request) {
        return bookUserRepository.findUserWith(request.getNickname(), request.getBookKey());
    }

    private Book findBook(CreateLineRequest request) {
        return bookRepository.findBookByBookKey(request.getBookKey())
            .orElseThrow(NotFoundBookException::new);
    }

    private Category findCategory(String bookKey,String name){
        return categoryRepository.findCategory(name,bookKey).orElseThrow(NotFoundCategoryException::new);
    }

}
