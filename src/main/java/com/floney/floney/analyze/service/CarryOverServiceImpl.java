package com.floney.floney.analyze.service;

import com.floney.floney.book.domain.category.CategoryType;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.CarryOver;
import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.CarryOverJdbcRepository;
import com.floney.floney.book.repository.analyze.CarryOverRepository;
import com.floney.floney.book.util.DateUtil;
import com.floney.floney.common.exception.book.NotFoundBookLineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.floney.floney.book.domain.category.CategoryType.TRANSFER;
import static com.floney.floney.common.constant.Status.ACTIVE;

@Service
@Transactional
@RequiredArgsConstructor
public class CarryOverServiceImpl implements CarryOverService {

    public final static int SAVE_CARRY_OVER_DURATION = 60;
    private final static int ONE_MONTH = 1;

    private final CarryOverRepository carryOverRepository;
    private final CarryOverJdbcRepository carryOverJdbcRepository;
    private final BookLineRepository bookLineRepository;

    @Override
    @Transactional(readOnly = true)
    public CarryOverInfo getCarryOverInfo(final Book book, final String date) {
        final boolean carryOverStatus = book.getCarryOverStatus();
        final LocalDate localDate = LocalDate.parse(date);

        // 1일일 경우, 이월 내역 포함하여 전송
        if (carryOverStatus && DateUtil.isFirstDay(date)) {
            final Optional<CarryOver> carryOverOptional = findCarryOver(localDate, book);
            if (carryOverOptional.isPresent()) {
                return CarryOverInfo.of(true, carryOverOptional.get());
            }
        }
        return CarryOverInfo.of(carryOverStatus, CarryOver.init());
    }

    // 가계부 내역 수정시
    @Override
    public void updateCarryOver(BookLineRequest request, BookLine savedBookLine) {
        deleteCarryOver(savedBookLine.getId());
        createCarryOver(savedBookLine);
    }

    @Override
    @Transactional
    public void createCarryOver(BookLine bookLine) {
        CategoryType categoryType = bookLine.getCategories().getLineCategory().getName();

        // 카테고리가 이체인 경우 생성 X
        if (TRANSFER.equals(categoryType)) {
            return;
        }

        // 현 내역 1달 후 부터 생성
        LocalDate targetDate = DateUtil.getFirstDayOfMonth(bookLine.getLineDate());

        List<CarryOver> carryOverList = new ArrayList<>();

        for (int i = 0; i < SAVE_CARRY_OVER_DURATION; i++) {
            targetDate = getNextMonth(targetDate);
            CarryOver carryOver = CarryOver.of(bookLine, targetDate);
            carryOverList.add(carryOver);
        }

        carryOverJdbcRepository.saveAll(carryOverList);
    }


    @Override
    @Transactional
    public void deleteCarryOver(final Long bookLineId) {
        final BookLine bookLine = bookLineRepository.findById(bookLineId).orElseThrow(NotFoundBookLineException::new);
        CategoryType categoryType = bookLine.getCategories().getLineCategory().getName();

        if (!bookLine.getBook().getCarryOverStatus() || TRANSFER.equals(categoryType)) {
            return;
        }

        LocalDate targetDate = DateUtil.getFirstDayOfMonth(bookLine.getLineDate());
        List<CarryOver> carryOverList = new ArrayList<>();

        for (int i = 0; i < SAVE_CARRY_OVER_DURATION; i++) {
            targetDate = getNextMonth(targetDate);
            CarryOver carryOver = CarryOver.of(bookLine, targetDate);
            carryOverList.add(carryOver.delete(categoryType));
        }

        carryOverJdbcRepository.saveAll(carryOverList);
    }


    private Optional<CarryOver> findCarryOver(final LocalDate targetDate, final Book bookLine) {
        return carryOverRepository.findCarryOverByDateAndBookAndStatus(targetDate, bookLine, ACTIVE);
    }

    private LocalDate getNextMonth(LocalDate targetDate) {
        return targetDate.plusMonths(ONE_MONTH);
    }
}
