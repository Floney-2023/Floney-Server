package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookLineResponse;
import com.floney.floney.book.dto.CreateLineRequest;
import com.floney.floney.book.entity.*;
import com.floney.floney.book.repository.BookLineCategoryRepository;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.common.exception.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.floney.floney.book.entity.AssetType.find;
import static com.floney.floney.book.entity.BookLineCategory.of;
import static com.floney.floney.book.service.CategoryEnum.*;

@Service
@RequiredArgsConstructor
public class BookLineServiceImpl implements BookLineService {
    private final BookRepository bookRepository;

    private final BookUserRepository bookUserRepository;

    private final BookLineRepository bookLineRepository;

    private final CategoryRepository categoryRepository;

    private final BookLineCategoryRepository bookLineCategoryRepository;

    @Override
    @Transactional
    public BookLineResponse createBookLine(CreateLineRequest request) {
        Book updatedBook = updateBudget(findBook(request), request);
        BookLine requestLine = request.to(findBookUser(request), updatedBook);

        BookLine savedLine = bookLineRepository.save(requestLine);
        findCategories(savedLine, request);

        BookLine newBookLine = bookLineRepository.save(savedLine);
        return BookLineResponse.of(newBookLine);
    }

    private Book updateBudget(Book book, CreateLineRequest request) {
        book.processTrans(find(request.getFlow()), request.getMoney());
        return book;
    }

    private void findCategories(BookLine bookLine, CreateLineRequest request) {
        bookLine.add(FLOW, saveBookLineCategory(bookLine, request, FLOW));
        bookLine.add(ASSET, saveBookLineCategory(bookLine, request, ASSET));
        bookLine.add(FLOW_LINE, saveBookLineCategory(bookLine, request, FLOW_LINE));
    }

    private BookLineCategory saveBookLineCategory(BookLine bookLine, CreateLineRequest request, CategoryEnum categoryEnum) {
        Category category = findCategory(request.getBookKey(), getCategoryFromRequest(categoryEnum, request));
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private String getCategoryFromRequest(CategoryEnum categoryEnum, CreateLineRequest request) {
        switch (categoryEnum) {
            case FLOW:
                return request.getFlow();
            case ASSET:
                return request.getAsset();
            case FLOW_LINE:
                return request.getLine();
            default:
                throw new NotFoundCategoryException();
        }
    }

    private BookUser findBookUser(CreateLineRequest request) {
        return bookUserRepository.findUserWith(request.getNickname(), request.getBookKey());
    }

    private Book findBook(CreateLineRequest request) {
        return bookRepository.findBookByBookKey(request.getBookKey())
            .orElseThrow(NotFoundBookException::new);
    }

    private Category findCategory(String bookKey, String name) {
        return categoryRepository.findCategory(name, bookKey)
            .orElseThrow(NotFoundCategoryException::new);
    }

}
