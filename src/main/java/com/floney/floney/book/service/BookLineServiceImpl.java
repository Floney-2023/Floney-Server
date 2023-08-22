package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.*;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.request.ChangeBookLineRequest;
import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.MonthLinesResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import com.floney.floney.book.entity.*;
import com.floney.floney.book.repository.BookLineCategoryRepository;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.CategoryRepository;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.floney.floney.book.dto.constant.CategoryEnum.*;
import static com.floney.floney.book.entity.BookLineCategory.of;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static java.time.LocalDate.parse;

@Service
@RequiredArgsConstructor
public class BookLineServiceImpl implements BookLineService {

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final CategoryFactory categoryFactory;

    @Override
    @Transactional
    public BookLineResponse createBookLine(String currentUser, CreateLineRequest request) {
        Book book = findBook(request.getBookKey());
        BookLine requestLine = request.to(findBookUser(currentUser, request), book);
        BookLine savedLine = bookLineRepository.save(requestLine);
        saveCategories(savedLine, request);

        BookLine newBookLine = bookLineRepository.save(savedLine);
        return BookLineResponse.of(newBookLine);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthLinesResponse showByMonth(String bookKey, String date) {
        DatesDuration dates = DateFactory.getDateDuration(date);
        Book book = findBook(bookKey);
        return MonthLinesResponse.of(date
            , daysExpense(bookKey, dates)
            , totalExpense(bookKey, dates)
            , CarryOverInfo.of(book));
    }

    @Override
    @Transactional(readOnly = true)
    public TotalDayLinesResponse showByDays(String bookKey, String date) {
        Book book = findBook(bookKey);
        List<DayLines> dayLines = DayLines.forDayView(bookLineRepository.allLinesByDay(parse(date), bookKey));
        List<TotalExpense> totalExpenses = bookLineRepository.totalExpenseByDay(parse(date), bookKey);

        return TotalDayLinesResponse.of(dayLines,
            totalExpenses,
            book.getSeeProfile(),
            CarryOverInfo.createIfFirstDay(book, date));
    }


    @Override
    @Transactional
    public void deleteAllLine(String bookKey) {
        bookLineRepository.deleteAllLines(bookKey);
    }

    @Override
    @Transactional
    public List<DayLines> allOutcomes(AllOutcomesRequest allOutcomesRequest) {
        return DayLines.forOutcomes(bookLineRepository.allOutcomes(allOutcomesRequest));
    }

    private void saveCategories(BookLine bookLine, CreateLineRequest request) {
        bookLine.add(FLOW, categoryFactory.saveFlowBookLineCategory(bookLine, request));
        bookLine.add(ASSET, categoryFactory.saveAssetBookLineCategory(bookLine, request));
        bookLine.add(FLOW_LINE, categoryFactory.saveLineBookLineCategory(bookLine, request));
    }

    private BookUser findBookUser(String currentUser, CreateLineRequest request) {
        return bookUserRepository.findBookUserByKey(currentUser, request.getBookKey())
            .orElseThrow(NotFoundBookUserException::new);
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(NotFoundBookException::new);
    }

    private List<BookLineExpense> daysExpense(String bookKey, DatesDuration dates) {
        return bookLineRepository.dayIncomeAndOutcome(bookKey, dates);
    }

    private Map<String, Long> totalExpense(String bookKey, DatesDuration dates) {
        return bookLineRepository.totalExpenseByMonth(bookKey, dates);
    }


}
