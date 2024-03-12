package com.floney.floney.analyze.service;

import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.common.domain.vo.DateDuration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.floney.floney.book.domain.category.CategoryType.INCOME;
import static com.floney.floney.book.domain.category.CategoryType.OUTCOME;
import static com.floney.floney.book.domain.entity.BookLine.START_DATE_OF_BOOK;

@Service
@Transactional
@RequiredArgsConstructor
public class CarryOverServiceImpl implements CarryOverService {
    private final BookLineRepository bookLineRepository;

    @Override
    @Transactional(readOnly = true)
    public double getCarryOver(String bookKey, String currentMonth) {
        DateDuration duration = new DateDuration(START_DATE_OF_BOOK, LocalDate.parse(currentMonth).minusDays(1));
        final double income = bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, duration, INCOME);
        final double outcome = bookLineRepository.totalMoneyByDurationAndCategoryType(bookKey, duration, OUTCOME);
        return income - outcome;
    }
}
