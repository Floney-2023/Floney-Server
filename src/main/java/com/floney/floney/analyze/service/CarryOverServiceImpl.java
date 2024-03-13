package com.floney.floney.analyze.service;

import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.common.domain.vo.DateDuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static com.floney.floney.book.domain.category.CategoryType.OUTCOME;

@Service
@Transactional
@RequiredArgsConstructor
public class CarryOverServiceImpl implements CarryOverService {

    private final BookLineRepository bookLineRepository;

    @Override
    @Transactional(readOnly = true)
    public double getCarryOver(String bookKey, String currentMonth) {
        DateDuration duration = new DateDuration(BookLine.START_DATE, LocalDate.parse(currentMonth).minusDays(1));
        final double income = bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, duration, INCOME);
        final double outcome = bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, duration, OUTCOME);
        return income - outcome;
    }
}
