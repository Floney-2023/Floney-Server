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

//    private void dayIncome(String bookKey){
//        bookLineRepository.dayIncome(bookKey);
//    }

    private Book updateBudget(Book book, CreateLineRequest request) {
        book.processTrans(find(request.getFlow()), request.getMoney());
        return book;
    }

    private void findCategories(BookLine bookLine, CreateLineRequest request) {
        bookLine.add(FLOW, saveFlowCategory(bookLine, request));
        bookLine.add(ASSET, saveAssetCategory(bookLine,request));
        bookLine.add(FLOW_LINE, saveLineCategory(bookLine, request));
    }

    private BookLineCategory saveLineCategory(BookLine bookLine, CreateLineRequest request) {
        Category category = categoryRepository.findLineCategory(request.getLine(),request.getBookKey(),request.getFlow())
            .orElseThrow(NotFoundCategoryException::new);
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private BookLineCategory saveFlowCategory(BookLine bookLine, CreateLineRequest request) {
        Category category = categoryRepository.findFlowCategory(request.getFlow());
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private BookLineCategory saveAssetCategory(BookLine bookLine,CreateLineRequest request){
        Category category = categoryRepository.findAssetCategory(request.getAsset());
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private BookUser findBookUser(CreateLineRequest request) {
        return bookUserRepository.findUserWith(request.getNickname(), request.getBookKey());
    }

    private Book findBook(CreateLineRequest request) {
        return bookRepository.findBookByBookKey(request.getBookKey())
            .orElseThrow(NotFoundBookException::new);
    }


}
