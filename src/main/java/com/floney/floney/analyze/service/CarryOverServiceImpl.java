package com.floney.floney.analyze.service;

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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.floney.floney.book.dto.constant.AssetType.BANK;
import static com.floney.floney.book.dto.constant.CategoryEnum.FLOW;
import static com.floney.floney.common.constant.Status.ACTIVE;

@RequiredArgsConstructor
@Component
public class CarryOverServiceImpl implements CarryOverService {
    private final static int SAVE_CARRY_OVER_DURATION = 60;
    private final static int ONE_MONTH = 1;

    private final CarryOverRepository carryOverRepository;

    private final BookLineRepository bookLineRepository;

    @Override
    public CarryOverInfo getCarryOverInfo(Book book, String date) {
        boolean carryOverStatus = book.getCarryOverStatus();
        LocalDate localDate = LocalDate.parse(date);
        // 1일일 경우, 이월 내역 포함하여 전송
        if (carryOverStatus && DateUtil.isFirstDay(date)) {
            Optional<CarryOver> carryOverOptional = carryOverRepository.findCarryOverByDateAndBookAndStatus(localDate, book, ACTIVE);
            if (carryOverOptional.isPresent()) {
                return CarryOverInfo.of(true, carryOverOptional.get());
            }
        }
        return CarryOverInfo.of(carryOverStatus, CarryOver.init());
    }

    // 가계부 내역 수정시
    @Transactional
    @Override
    public void updateCarryOver(BookLineRequest request, BookLine savedBookLine) {
        deleteCarryOver(savedBookLine.getId());
        createCarryOverByAddBookLine(request, savedBookLine.getBook());
    }

    @Transactional
    @Override
    public void createCarryOverByAddBookLine(BookLineRequest request, Book book) {
        LocalDate targetDate = DateUtil.getFirstDayOfMonth(request.getLineDate());
        List<CarryOver> carryOvers = new ArrayList<>();

        // 다음달부터 생성
        targetDate = getNextMonth(targetDate);

        // 5년(60개월) 동안의 엔티티 생성
        for (int i = 0; i < SAVE_CARRY_OVER_DURATION; i++) {
            Optional<CarryOver> savedCarryOver = carryOverRepository.findCarryOverByDateAndBookAndStatus(targetDate, book, ACTIVE);

            if (savedCarryOver.isEmpty() && !Objects.equals(request.getFlow(), BANK.getKind())) {
                CarryOver newCarryOver = CarryOver.of(request, book, targetDate);
                carryOvers.add(newCarryOver);
            } else {
                savedCarryOver.ifPresent(carryOver -> {
                    carryOver.update(request.getMoney(), request.getFlow());
                    carryOvers.add(carryOver);
                });
            }
            targetDate = getNextMonth(targetDate);
        }

        carryOverRepository.saveAll(carryOvers);
    }

    @Override
    @Transactional
    public void deleteCarryOver(Long bookLineId) {
        BookLine savedBookLine = bookLineRepository.findById(bookLineId)
            .orElseThrow(NotFoundBookLineException::new);

        if (!savedBookLine.getBook().getCarryOverStatus()) {
            return;
        }

        LocalDate targetDate = DateUtil.getFirstDayOfMonth(savedBookLine.getLineDate());
        targetDate = getNextMonth(targetDate);
        List<CarryOver> carryOvers = new ArrayList<>();

        // 5년(60개월) 동안의 엔티티 생성
        for (int i = 0; i < SAVE_CARRY_OVER_DURATION; i++) {
            Optional<CarryOver> savedCarryOver = carryOverRepository.findCarryOverByDateAndBookAndStatus(targetDate, savedBookLine.getBook(), ACTIVE);
            savedCarryOver.ifPresent(carryOver -> {
                carryOver.delete(savedBookLine.getMoney(), savedBookLine.getBookLineCategories().get(FLOW));
                carryOvers.add(carryOver);
            });
            targetDate = getNextMonth(targetDate);
        }

        carryOverRepository.saveAll(carryOvers);
    }

    private LocalDate getNextMonth(LocalDate targetDate) {
        return targetDate.plusMonths(ONE_MONTH);
    }
}
