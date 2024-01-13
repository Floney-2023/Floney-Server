package com.floney.floney.book.service;

import com.floney.floney.analyze.service.AssetService;
import com.floney.floney.analyze.service.CarryOverServiceImpl;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.BookUser;
import com.floney.floney.book.domain.vo.MonthLinesResponse;
import com.floney.floney.book.dto.process.BookLineExpense;
import com.floney.floney.book.dto.process.DayLines;
import com.floney.floney.book.dto.process.TotalExpense;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.service.category.CategoryFactory;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public BookLineResponse createBookLine(final String email, final BookLineRequest request) {
        final Book book = findBook(request.getBookKey());
        final BookLine bookLine = request.to(findBookUser(email, request), book);
        bookLineRepository.save(bookLine);

        if (book.getCarryOverStatus()) {
            carryOverFactory.createCarryOverByAddBookLine(request, book);
        }
        categoryFactory.saveCategories(bookLine, request);
        if (bookLine.includedInAsset()) {
            assetService.addAssetOf(request, book);
        }

        return BookLineResponse.from(bookLine);
    }

    @Override
    public MonthLinesResponse showByMonth(String bookKey, String date) {
        Book book = findBook(bookKey);
        DateDuration dates = DateDuration.getStartAndEndOfMonth(date);

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
        // TODO: BookLineRequest에 bookKey 삭제 후 아래 메서드 삭제
        validateBookLineIncludedInBook(bookLine.getBook(), book);

        if (bookLine.includedInAsset()) {
            assetService.subtractAssetOf(bookLine.getId());
        }

        // 가계부 내역 갱신
        bookLine.update(request);

        // 가계부 내역 갱신에 따른 관련 데이터들 갱신
        if (book.getCarryOverStatus()) {
            carryOverFactory.updateCarryOver(request, bookLine);
        }
        if (bookLine.includedInAsset()) {
            assetService.addAssetOf(request, book);
        }
        categoryFactory.changeCategories(bookLine, request);

        return BookLineResponse.from(bookLine);
    }

    @Override
    @Transactional
    public void deleteLine(final Long bookLineId) {
        final BookLine savedBookLine = bookLineRepository.findByIdAndStatus(bookLineId, ACTIVE)
            .orElseThrow(NotFoundBookLineException::new);
        savedBookLine.inactive();
    }

    private void validateBookLineIncludedInBook(final Book bookOfBookLine, final Book book) {
        if (!Objects.equals(bookOfBookLine.getId(), book.getId())) {
            throw new RuntimeException("가계부 내역이 해당 가계부에 속하지 않습니다");
        }
    }

    private BookUser findBookUser(String currentUser, BookLineRequest request) {
        return bookUserRepository.findBookUserByKey(currentUser, request.getBookKey())
            .orElseThrow(() -> new NotFoundBookUserException(request.getBookKey(), currentUser));
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private List<BookLineExpense> daysExpense(String bookKey, DateDuration dates) {
        return bookLineRepository.dayIncomeAndOutcome(bookKey, dates);
    }

    private Map<String, Double> totalExpense(String bookKey, DateDuration dates) {
        return bookLineRepository.totalExpenseByMonth(bookKey, dates);
    }
}
