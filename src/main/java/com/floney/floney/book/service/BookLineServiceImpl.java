package com.floney.floney.book.service;

import com.floney.floney.analyze.service.AssetServiceImpl;
import com.floney.floney.analyze.service.CarryOverServiceImpl;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DatesDuration;
import com.floney.floney.book.dto.process.DayLines;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.request.ChangeBookLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.MonthLinesResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.entity.BookUser;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.category.BookLineCategoryRepository;
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
    private final AssetServiceImpl assetFactory;
    private final BookLineCategoryRepository bookLineCategoryRepository;

    @Override
    @Transactional
    public BookLineResponse createBookLine(String currentUser, ChangeBookLineRequest request) {
        Book book = findBook(request.getBookKey());

        // 이월 ON 일시, 이월 내역 갱신
        if (book.getCarryOverStatus()) {
            carryOverFactory.createCarryOverByAddBookLine(request, book);
        }

        // 자산 갱신
        assetFactory.createAssetBy(request, book);
        BookLine requestLine = request.to(findBookUser(currentUser, request), book);
        BookLine savedLine = bookLineRepository.save(requestLine);
        categoryFactory.saveCategories(savedLine, request);

        BookLine newBookLine = bookLineRepository.save(savedLine);
        return BookLineResponse.of(newBookLine);
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
        return DayLines.forOutcomes(bookLineRepository.allOutcomes(allOutcomesRequest));
    }

    @Override
    @Transactional
    public BookLineResponse changeLine(ChangeBookLineRequest request) {
        BookLine bookLine = bookLineRepository.findByIdWithCategories(request.getLineId())
            .orElseThrow(NotFoundBookLineException::new);
        Book book = findBook(request.getBookKey());

        // 이월설정이 ON 일시, 이월 설정 재갱신
        if (book.getCarryOverStatus()) {
            carryOverFactory.updateCarryOver(request, bookLine);
        }

        // 자산 내역 갱신
        assetFactory.updateAsset(request, bookLine);
        categoryFactory.changeCategories(bookLine, request);
        bookLine.update(request);
        BookLine savedBookLine = bookLineRepository.save(bookLine);
        return BookLineResponse.changeResponse(savedBookLine, bookLine.getWriter());
    }

    @Override
    @Transactional
    public void deleteLine(final Long bookLineId) {
        final BookLine savedBookLine = bookLineRepository.findByIdAndStatus(bookLineId, ACTIVE)
            .orElseThrow(NotFoundBookLineException::new);

        if (savedBookLine.getBook().getCarryOverStatus()) {
            carryOverFactory.deleteCarryOver(savedBookLine);
        }

        assetFactory.deleteAsset(savedBookLine);
        savedBookLine.inactive();
        bookLineCategoryRepository.inactiveAllByBookLineId(bookLineId);
    }

    private BookUser findBookUser(String currentUser, ChangeBookLineRequest request) {
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

    private Map<String, Float> totalExpense(String bookKey, DatesDuration dates) {
        return bookLineRepository.totalExpenseByMonth(bookKey, dates);
    }
}
