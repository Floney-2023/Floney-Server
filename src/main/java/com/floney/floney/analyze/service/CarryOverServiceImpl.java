package com.floney.floney.analyze.service;

import com.floney.floney.book.domain.category.entity.Category;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.domain.entity.CarryOver;
import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.repository.BookLineRepository;
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

    private final static int SAVE_CARRY_OVER_DURATION = 60;
    private final static int ONE_MONTH = 1;

    private final CarryOverRepository carryOverRepository;
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
        createCarryOverByAddBookLine(savedBookLine);
    }

    @Override
    public void createCarryOverByAddBookLine(BookLine bookLine) {
        Book book = bookLine.getBook();
        LocalDate targetDate = DateUtil.getFirstDayOfMonth(bookLine.getLineDate());
        List<CarryOver> carryOvers = new ArrayList<>();

        // 다음달부터 생성
        targetDate = getNextMonth(targetDate);

        Category lineCategory = bookLine.getCategories().getLineCategory();
        // 5년(60개월) 동안의 엔티티 생성
        for (int i = 0; i < SAVE_CARRY_OVER_DURATION; i++) {
            final Optional<CarryOver> savedCarryOver = findCarryOver(targetDate, book);

            if (savedCarryOver.isEmpty() && !TRANSFER.getMeaning().equals(lineCategory.toString())) {
                CarryOver newCarryOver = CarryOver.of(bookLine, targetDate);
                carryOvers.add(newCarryOver);
            } else {
                savedCarryOver.ifPresent(carryOver -> {
                    carryOver.update(bookLine.getMoney(), lineCategory.getName().getMeaning());
                    carryOvers.add(carryOver);
                });
            }
            targetDate = getNextMonth(targetDate);
        }

        carryOverRepository.saveAll(carryOvers);
    }


    @Override
    public void deleteCarryOver(final Long bookLineId) {
        final BookLine bookLine = bookLineRepository.findById(bookLineId)
            .orElseThrow(NotFoundBookLineException::new);

        if (!bookLine.getBook().getCarryOverStatus()) {
            return;
        }

        LocalDate targetDate = DateUtil.getFirstDayOfMonth(bookLine.getLineDate());

        for (int i = 0; i <= SAVE_CARRY_OVER_DURATION; i++) {
            targetDate = getNextMonth(targetDate);
            final Optional<CarryOver> savedCarryOver = findCarryOver(targetDate, bookLine.getBook());
            savedCarryOver.ifPresent(carryOver -> carryOver.delete(bookLine.getMoney(), bookLine.getCategories()));
        }
    }

    private Optional<CarryOver> findCarryOver(final LocalDate targetDate, final Book bookLine) {
        return carryOverRepository.findCarryOverByDateAndBookAndStatus(targetDate, bookLine, ACTIVE);
    }

    private LocalDate getNextMonth(LocalDate targetDate) {
        return targetDate.plusMonths(ONE_MONTH);
    }
}
