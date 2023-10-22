package com.floney.floney.book.dto.process;

import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.CarryOver;
import com.floney.floney.book.repository.CarryOverRepository;
import com.floney.floney.book.util.DateFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.floney.floney.book.dto.constant.AssetType.BANK;

@RequiredArgsConstructor
@Component
public class CarryOverFactory {
    private static final int FIVE_YEARS = 60;
    private final CarryOverRepository carryOverRepository;

    public CarryOverInfo getCarryOverInfo(Book book, String date) {
        boolean carryOverStatus = book.getCarryOverStatus();
        LocalDate localDate = LocalDate.parse(date);
        // 1일일 경우, 이월 내역 포함하여 전송
        if (carryOverStatus && DateFactory.isFirstDay(date)) {
            Optional<CarryOver> carryOverOptional = carryOverRepository.findCarryOverByDateAndBook(localDate, book);
            if (carryOverOptional.isPresent()) {
                return CarryOverInfo.of(true, carryOverOptional.get());
            }
        }

        return CarryOverInfo.of(carryOverStatus, CarryOver.init());
    }


    @Transactional
    public void updateCarryOver(CreateLineRequest request, Book book) {
        LocalDate targetDate = DateFactory.getFirstDayOf(request.getLineDate());
        List<CarryOver> carryOvers = new ArrayList<>();

        // 다음달부터 생성
        targetDate = targetDate.plusMonths(1);

        // 5년(60개월) 동안의 엔티티 생성
        for (int i = 0; i < FIVE_YEARS; i++) {
            Optional<CarryOver> savedCarryOver = carryOverRepository.findCarryOverByDateAndBook(targetDate, book);

            if (savedCarryOver.isEmpty() && !Objects.equals(request.getFlow(), BANK.name())) {
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
