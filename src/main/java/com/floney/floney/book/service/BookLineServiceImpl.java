package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.*;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.request.ChangeBookLineRequest;
import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.MonthLinesResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.entity.CarryOver;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.CarryOverRepository;
import com.floney.floney.book.repository.category.BookLineCategoryCustomRepository;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static java.time.LocalDate.parse;

@Service
@RequiredArgsConstructor
public class BookLineServiceImpl implements BookLineService {
    private static final int FIVE_YEARS = 60;
    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final CategoryFactory categoryFactory;
    private final CarryOverFactory carryOverFactory;
    private final BookLineCategoryCustomRepository bookLineCategoryRepository;
    private final CarryOverRepository carryOverRepository;

    @Override
    @Transactional
    public BookLineResponse createBookLine(String currentUser, CreateLineRequest request) {
        Book book = findBook(request.getBookKey());

        if (book.getCarryOverStatus()) {
            updateCarryOver(request, book);
        }

        BookLine requestLine = request.to(findBookUser(currentUser, request), book);
        BookLine savedLine = bookLineRepository.save(requestLine);
        categoryFactory.saveCategories(savedLine, request);

        BookLine newBookLine = bookLineRepository.save(savedLine);
        return BookLineResponse.of(newBookLine);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthLinesResponse showByMonth(String bookKey, String date) {
        Book book = findBook(bookKey);
        DatesDuration dates = DateFactory.getDateDuration(date);

        return MonthLinesResponse.of(date
            , daysExpense(bookKey, dates)
            , totalExpense(bookKey, dates)
            , carryOverFactory.getCarryOverInfo(book, date));
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
            carryOverFactory.getCarryOverInfo(book, date));
    }

    @Override
    @Transactional
    public List<DayLines> allOutcomes(AllOutcomesRequest allOutcomesRequest) {
        return DayLines.forOutcomes(bookLineRepository.allOutcomes(allOutcomesRequest));
    }

    @Override
    public BookLineResponse changeLine(ChangeBookLineRequest request) {
        BookLine bookLine = bookLineRepository.findByIdWithCategories(request.getLineId())
            .orElseThrow(() -> new NotFoundBookLineException());
        categoryFactory.changeCategories(bookLine, request);
        bookLine.update(request);
        BookLine savedBookLine = bookLineRepository.save(bookLine);
        return BookLineResponse.changeResponse(savedBookLine, bookLine.getWriter());
    }

    @Override
    public void deleteLine(Long bookLineKey) {
        BookLine savedBookLine = bookLineRepository.findByIdAndStatus(bookLineKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookLineException());
        savedBookLine.delete();
        bookLineCategoryRepository.deleteBookLineCategoryById(bookLineKey);
        bookLineRepository.save(savedBookLine);
    }

    private BookUser findBookUser(String currentUser, CreateLineRequest request) {
        return bookUserRepository.findBookUserByKey(currentUser, request.getBookKey())
            .orElseThrow(() -> new NotFoundBookUserException(request.getBookKey(), currentUser));
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private List<BookLineExpense> daysExpense(String bookKey, DatesDuration dates) {
        return bookLineRepository.dayIncomeAndOutcome(bookKey, dates);
    }

    private Map<String, Long> totalExpense(String bookKey, DatesDuration dates) {
        return bookLineRepository.totalExpenseByMonth(bookKey, dates);
    }

    private void updateCarryOver(CreateLineRequest request, Book book) {
        LocalDate targetDate = DateFactory.getFirstDayOf(request.getLineDate());
        List<CarryOver> carryOvers = new ArrayList<>();

        // 5년(60개월) 동안의 엔티티 생성
        for (int i = 0; i < FIVE_YEARS; i++) {
            Optional<CarryOver> savedCarryOver = carryOverRepository.findCarryOverByDate(targetDate);

            if (savedCarryOver.isEmpty()) {
                CarryOver newCarryOver = CarryOver.of(request, book, targetDate);
                carryOvers.add(newCarryOver);
            } else {
                CarryOver saved = savedCarryOver.get();
                saved.update(request.getMoney(), request.getFlow());
                carryOvers.add(saved);
            }
            targetDate = targetDate.plusMonths(1);
        }

        carryOverRepository.saveAll(carryOvers);
    }


}
