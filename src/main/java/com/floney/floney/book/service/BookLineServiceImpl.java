package com.floney.floney.book.service;

import com.floney.floney.book.dto.*;
import com.floney.floney.book.entity.*;
import com.floney.floney.book.repository.BookLineCategoryRepository;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.exception.NotFoundBookException;
import com.floney.floney.common.exception.NotFoundBookUserException;
import com.floney.floney.common.exception.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.floney.floney.book.dto.constant.CategoryEnum.*;
import static com.floney.floney.book.entity.BookLineCategory.of;
import static com.floney.floney.book.util.DateFactory.END;
import static com.floney.floney.book.util.DateFactory.START;
import static java.time.LocalDate.parse;

@Service
@RequiredArgsConstructor
public class BookLineServiceImpl implements BookLineService {

    private final BookRepository bookRepository;

    private final BookUserRepository bookUserRepository;

    private final BookLineRepository bookLineRepository;

    private final CategoryRepository categoryRepository;

    private final BookLineCategoryRepository bookLineCategoryRepository;

    private static final boolean ACTIVE = true;

    @Override
    @Transactional
    public BookLineResponse createBookLine(CreateLineRequest request) {
        Book book = findBook(request);
        if (request.isNotExcept()) {
            book.processTrans(request);
        }
        BookLine requestLine = request.to(findBookUser(request), book);
        BookLine savedLine = bookLineRepository.save(requestLine);
        findCategories(savedLine, request);

        BookLine newBookLine = bookLineRepository.save(savedLine);
        return BookLineResponse.of(newBookLine);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthLinesResponse showByMonth(String bookKey, String date) {
        Map<String, LocalDate> dates = DateFactory.getDate(date);
        return MonthLinesResponse.of(date, daysExpense(bookKey, dates)
            , totalExpense(bookKey, dates));
    }

    @Override
    @Transactional(readOnly = true)
    public TotalDayLinesResponse showByDays(String bookKey, String date) {
        return TotalDayLinesResponse.of(DayLines.of(bookLineRepository.allLinesByDay(parse(date), bookKey)), bookLineRepository.totalExpenseByDay(parse(date), bookKey));
    }

    private void findCategories(BookLine bookLine, CreateLineRequest request) {
        bookLine.add(FLOW, saveFlowCategory(bookLine, request));
        bookLine.add(ASSET, saveAssetCategory(bookLine, request));
        bookLine.add(FLOW_LINE, saveLineCategory(bookLine, request));
    }

    private BookLineCategory saveLineCategory(BookLine bookLine, CreateLineRequest request) {
        Category category = categoryRepository.findLineCategory(request.getLine(), request.getBookKey(), request.getFlow())
            .orElseThrow(NotFoundCategoryException::new);
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private BookLineCategory saveFlowCategory(BookLine bookLine, CreateLineRequest request) {
        Category category = categoryRepository.findFlowCategory(request.getFlow());
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private BookLineCategory saveAssetCategory(BookLine bookLine, CreateLineRequest request) {
        Category category = categoryRepository.findAssetCategory(request.getAsset());
        return bookLineCategoryRepository.save(of(bookLine, category));
    }

    private BookUser findBookUser(CreateLineRequest request) {
        return bookUserRepository.findUserWith(request.getNickname(), request.getBookKey())
            .orElseThrow(NotFoundBookUserException::new);
    }

    private Book findBook(CreateLineRequest request) {
        return bookRepository.findBookByBookKeyAndStatus(request.getBookKey(), ACTIVE)
            .orElseThrow(NotFoundBookException::new);
    }

    private List<BookLineExpense> daysExpense(String bookKey, Map<String, LocalDate> dates) {
        return bookLineRepository.dayIncomeAndOutcome(bookKey, dates);
    }

    private List<TotalExpense> totalExpense(String bookKey, Map<String, LocalDate> dates) {
        return bookLineRepository.totalExpense(bookKey, dates);
    }


}
