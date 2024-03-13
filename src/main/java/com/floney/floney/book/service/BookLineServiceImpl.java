package com.floney.floney.book.service;

import com.floney.floney.analyze.service.CarryOverService;
import com.floney.floney.book.domain.RepeatDuration;
import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.category.entity.Subcategory;
import com.floney.floney.book.domain.entity.*;
import com.floney.floney.book.domain.vo.MonthLinesResponse;
import com.floney.floney.book.dto.process.*;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.book.repository.RepeatBookLineRepository;
import com.floney.floney.book.repository.category.CategoryCustomRepository;
import com.floney.floney.common.domain.vo.DateDuration;
import com.floney.floney.common.exception.book.NotFoundBookException;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import com.floney.floney.common.exception.book.NotFoundCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.floney.floney.book.domain.category.CategoryType.*;
import static com.floney.floney.common.constant.Status.ACTIVE;
import static java.time.LocalDate.parse;

@Service
@Transactional
@RequiredArgsConstructor
public class BookLineServiceImpl implements BookLineService {

    private final BookRepository bookRepository;
    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;
    private final CategoryCustomRepository categoryRepository;
    private final RepeatBookLineRepository repeatBookLineRepository;
    private final CarryOverService carryOverService;

    @Override
    @Transactional
    public BookLineResponse createBookLine(final String email, final BookLineRequest request) {
        final Book book = findBook(request.getBookKey());
        final BookUser bookUser = findBookUser(email, request);
        final BookLineCategory bookLineCategory = findCategories(request, book);
        final BookLine bookLine = bookLineRepository.save(request.to(bookUser, bookLineCategory));

        // 반복 내역인 경우
        if (request.getRepeatDuration() != RepeatDuration.NONE) {
            return createBookLineByRepeat(bookLine, request.getRepeatDuration());
        } else {
            return createBookLineByNotRepeat(bookLine);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MonthLinesResponse showByMonth(final String bookKey, final String date) {
        final Book book = findBook(bookKey);
        final DateDuration dates = DateDuration.startAndEndOfMonth(date);
        final CarryOverInfo carryOverInfo = new CarryOverInfo(book.getCarryOverStatus(), carryOverService.getCarryOver(bookKey, date));
        return MonthLinesResponse.of(date, daysExpense(bookKey, dates), totalExpense(bookKey, dates), carryOverInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public TotalDayLinesResponse showByDays(final String bookKey, final String date) {
        final Book book = findBook(bookKey);
        final LocalDate day = parse(date);

        final List<BookLineWithWriterView> bookLinesOfDay = bookLineRepository.allLinesByDay(day, bookKey);

        final List<TotalExpense> totalExpensesOfDay = List.of(bookLineRepository.totalMoneyByDateAndCategoryType(bookKey, day, INCOME), bookLineRepository.totalMoneyByDateAndCategoryType(bookKey, day, OUTCOME));
        final CarryOverInfo carryOverInfo = new CarryOverInfo(book.getCarryOverStatus(), carryOverService.getCarryOver(bookKey, date));
        return TotalDayLinesResponse.of(bookLinesOfDay, totalExpensesOfDay, book.getSeeProfile(), carryOverInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DayLines> allOutcomes(final AllOutcomesRequest allOutcomesRequest) {
        return bookLineRepository.findAllOutcomes(allOutcomesRequest)
            .stream()
            .map(DayLines::from)
            .toList();
    }

    @Override
    @Transactional
    public BookLineResponse changeLine(final BookLineRequest request) {
        final BookLine bookLine = bookLineRepository.findByIdWithCategoriesAndWriter(request.getLineId())
            .orElseThrow(NotFoundBookLineException::new);

        final Book book = findBook(request.getBookKey());

        // TODO: BookLineRequest에 bookKey 삭제 후 아래 메서드 삭제
        validateBookLineIncludedInBook(bookLine.getBook(), book);

        // 가계부 내역 갱신
        bookLine.update(request);
        bookLineRepository.save(bookLine);

        // TODO: CategoryService 로 이동
        updateCategory(bookLine.getCategories(), request.getLine(), request.getAsset());
        bookLineRepository.save(bookLine);
        return BookLineResponse.from(bookLine);
    }

    @Override
    @Transactional
    public void deleteLine(final Long bookLineId) {
        final BookLine savedBookLine = bookLineRepository.findByIdAndStatus(bookLineId, ACTIVE).orElseThrow(NotFoundBookLineException::new);
        savedBookLine.inactive();
        bookLineRepository.save(savedBookLine);
    }

    @Override
    @Transactional
    public void deleteAllAfterBookLineByRepeat(final long bookLineId) {
        final BookLine savedBookLine = bookLineRepository.findByIdAndStatus(bookLineId, ACTIVE)
            .orElseThrow(NotFoundBookLineException::new);

        //TODO : 더 어울리는 예외 처리
        if (savedBookLine.isNotRepeat()) {
            throw new NotFoundBookLineException();
        }

        List<BookLine> bookLines = bookLineRepository.findAllRepeatBookLineByEqualOrAfter(savedBookLine.getLineDate(), savedBookLine.getRepeatBookLine());
        bookLines.forEach(BookLine::inactive);
        bookLineRepository.saveAll(bookLines);
    }

    private BookLineResponse createBookLineByNotRepeat(final BookLine bookLine) {
        Book book = bookLine.getBook();
        bookLineRepository.save(bookLine);

        return BookLineResponse.from(bookLine);
    }

    private BookLineResponse createBookLineByRepeat(final BookLine bookLine, final RepeatDuration requestRepeatDuration) {
        Book book = bookLine.getBook();

        // 1. 반복 내역 객체 생성
        RepeatBookLine repeatBookLine = RepeatBookLine.of(bookLine, requestRepeatDuration);

        // 2. 가계부 내역 - 반복 내역 매핑
        bookLine.repeatBookLine(repeatBookLine);

        // 3. 반복 내역 생성
        List<BookLine> bookLines = repeatBookLine.bookLinesBy(requestRepeatDuration);
        bookLines.add(bookLine);

        // 3.  저장
        repeatBookLineRepository.save(repeatBookLine);
        bookLineRepository.saveAll(bookLines);

        return BookLineResponse.from(bookLine);
    }

    private BookLineCategory findCategories(final BookLineRequest request, final Book book) {
        final String categoryName = request.getFlow(); //LineType
        final Category lineCategory = findLineCategory(categoryName);

        final Subcategory lineSubcategory = findLineSubCategory(request.getLine(), lineCategory, book);
        final Subcategory assetSubcategory = findAssetSubCategory(book, request.getAsset());
        return BookLineCategory.create(lineCategory, lineSubcategory, assetSubcategory);
    }

    private void updateCategory(final BookLineCategory bookLineCategory, final String lineSubCategoryName, final String assetSubCategoryName) {
        updateLineSubCategory(bookLineCategory, lineSubCategoryName);
        updateAssetSubCategory(bookLineCategory, assetSubCategoryName);
    }

    private void updateAssetSubCategory(final BookLineCategory bookLineCategory, final String assetSubCategoryName) {
        final Book book = bookLineCategory.getBookLine().getBook();
        final Subcategory assetSubcategory = findAssetSubCategory(book, assetSubCategoryName);
        bookLineCategory.updateAssetSubCategory(assetSubcategory);
    }

    private void updateLineSubCategory(final BookLineCategory bookLineCategory, final String lineSubCategoryName) {
        final Book book = bookLineCategory.getBookLine().getBook();
        final Category lineCategory = bookLineCategory.getLineCategory();
        final Subcategory lineSubcategory = findLineSubCategory(lineSubCategoryName, lineCategory, book);
        bookLineCategory.updateLineSubCategory(lineSubcategory);
    }

    private Category findLineCategory(final String categoryName) {
        final CategoryType categoryType = CategoryType.findLineByMeaning(categoryName);
        return categoryRepository.findByType(categoryType)
            .orElseThrow(() -> new NotFoundCategoryException(categoryName));
    }

    private Subcategory findLineSubCategory(final String lineSubCategoryName, final Category lineCategory, final Book book) {
        return categoryRepository.findSubcategory(lineSubCategoryName, book, lineCategory.getName())
            .orElseThrow(() -> new NotFoundCategoryException(lineSubCategoryName));
    }

    private Subcategory findAssetSubCategory(final Book book, final String assetSubCategoryName) {
        return categoryRepository.findSubcategory(assetSubCategoryName, book, ASSET)
            .orElseThrow(() -> new NotFoundCategoryException(assetSubCategoryName));
    }

    private void validateBookLineIncludedInBook(final Book bookOfBookLine, final Book book) {
        if (!Objects.equals(bookOfBookLine.getId(), book.getId())) {
            throw new RuntimeException("가계부 내역이 해당 가계부에 속하지 않습니다");
        }
    }

    private BookUser findBookUser(String currentUser, BookLineRequest request) {
        return bookUserRepository.findBookUserByEmailAndBookKey(currentUser, request.getBookKey())
            .orElseThrow(() -> new NotFoundBookUserException(request.getBookKey(), currentUser));
    }

    private Book findBook(String bookKey) {
        return bookRepository.findBookByBookKeyAndStatus(bookKey, ACTIVE)
            .orElseThrow(() -> new NotFoundBookException(bookKey));
    }

    private List<BookLineExpense> daysExpense(final String bookKey, final DateDuration dates) {
        return bookLineRepository.findIncomeAndOutcomeByDurationPerDay(bookKey, dates);
    }

    private Map<String, Double> totalExpense(String bookKey, DateDuration dates) {
        final double income = bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, dates, INCOME);
        final double outcome = bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, dates, OUTCOME);
        // TODO: Map 대신 새로운 객체 생성
        return Map.of(INCOME.getMeaning(), income, OUTCOME.getMeaning(), outcome);
    }
}
