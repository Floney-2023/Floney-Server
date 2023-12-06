package com.floney.floney.book.service;

import com.floney.floney.analyze.service.AssetService;
import com.floney.floney.analyze.service.CarryOverServiceImpl;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.process.DayLines;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.MonthLinesResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.service.category.CategoryFactory;
import com.floney.floney.book.util.DateFactory;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.floney.floney.common.constant.Status.ACTIVE;
import static java.time.LocalDate.parse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookLineServiceImpl implements BookLineService {

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final CategoryFactory categoryFactory;
    private final CarryOverServiceImpl carryOverFactory;
    private final AssetService assetService;

    @Override
    @Transactional
    public BookLineResponse createBookLine(String currentUser, BookLineRequest request) {
        Book book = findBook(request.getBookKey());

        // 이월 ON 일시, 이월 내역 갱신
        if (book.getCarryOverStatus()) {
            carryOverFactory.createCarryOverByAddBookLine(request, book);
        }

        // 자산 갱신
        assetService.createAsset(request, book);
        BookLine requestLine = request.to(findBookUser(currentUser, request), book);
        BookLine savedLine = bookLineRepository.save(requestLine);
        categoryFactory.saveCategories(savedLine, request);

        BookLine newBookLine = bookLineRepository.save(savedLine);
        return BookLineResponse.from(newBookLine);
    }

    @Override
    public MonthLinesResponse showByMonth(String bookKey, String date) {
        Book book = findBook(bookKey);
        DatesDuration dates = DateFactory.getDateDuration(date);

        return MonthLinesResponse.of(
            date,
            daysExpense(bookKey, dates),
            totalExpense(bookKey, dates),
            carryOverFactory.getCarryOverInfo(book, date)
        );
    }

    @Override
    public TotalDayLinesResponse showByDays(String bookKey, String date) {
        Book book = findBook(bookKey);

        List<DayLines> dayLines = DayLines.forDayView(bookLineRepository.allLinesByDay(parse(date), bookKey));
        List<TotalExpense> totalExpenses = bookLineRepository.totalExpenseByDay(parse(date), bookKey);

        return TotalDayLinesResponse.of(
            dayLines,
            totalExpenses,
            book.getSeeProfile(),
            carryOverFactory.getCarryOverInfo(book, date)
        );
    }

    @Override
    @Transactional
    public List<DayLines> allOutcomes(AllOutcomesRequest allOutcomesRequest) {
        return DayLines.forOutcomes(bookLineRepository.getAllLines(allOutcomesRequest));
    }

    @Override
    @Transactional
    public BookLineResponse changeLine(final BookLineRequest request) {
        final BookLine bookLine = bookLineRepository.findByIdWithCategories(request.getLineId())
            .orElseThrow(NotFoundBookLineException::new);
        final Book book = findBook(request.getBookKey());

        // 이월 여부에 따른 이월 데이터 갱신
        if (book.getCarryOverStatus()) {
            carryOverFactory.updateCarryOver(request, bookLine);
        }

        // 자산 데이터 갱신
        assetService.deleteAsset(bookLine.getId());
        assetService.createAsset(request, book);

        // 카테고리 데이터 갱신
        categoryFactory.changeCategories(bookLine, request);

        // 가계부 내역 갱신
        bookLine.update(request);

        return BookLineResponse.from(bookLine);
    }

    @Override
    @Transactional
    public void deleteLine(final Long bookLineId) {
        final BookLine savedBookLine = bookLineRepository.findByIdAndStatus(bookLineId, ACTIVE)
            .orElseThrow(NotFoundBookLineException::new);
        savedBookLine.inactive();
    }

    private BookUser findBookUser(String currentUser, BookLineRequest request) {
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

    private Map<String, Double> totalExpense(String bookKey, DatesDuration dates) {
        return bookLineRepository.totalExpenseByMonth(bookKey, dates);
    }
}
