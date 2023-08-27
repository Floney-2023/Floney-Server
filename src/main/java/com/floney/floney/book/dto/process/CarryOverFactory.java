package com.floney.floney.book.dto.process;

import com.floney.floney.book.entity.Book;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.util.DateFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

import static com.floney.floney.book.dto.constant.AssetType.INCOME;
import static com.floney.floney.book.dto.constant.AssetType.OUTCOME;

@RequiredArgsConstructor
@Component
public class CarryOverFactory {
    private final static long INACTIVE_CARRY_OVER = 0L;
    private final static long DEFAULT_EXPENSE = 0L;
    private final BookLineRepository bookLineRepository;

    public CarryOverInfo getCarryOverInfo(Book book, String date) {
        boolean status = book.getCarryOverStatus();

        if (DateFactory.isFirstDay(date) && status) {
            DatesDuration datesDuration = DateFactory.getBeforeDateDuration(LocalDate.parse(date));
            return CarryOverInfo.of(status, getCarryOverMoney(book.getBookKey(), datesDuration));
        }
        return CarryOverInfo.of(status, INACTIVE_CARRY_OVER);
    }

    private long getCarryOverMoney(String bookKey, DatesDuration duration) {
        Map<String, Long> totalExpense = bookLineRepository.totalExpenseByMonth(bookKey, duration);
        long carryOverMoney = totalExpense.getOrDefault(INCOME.getKind(),DEFAULT_EXPENSE) - totalExpense.getOrDefault(OUTCOME.getKind(),DEFAULT_EXPENSE);
        return carryOverMoney;
    }
}
