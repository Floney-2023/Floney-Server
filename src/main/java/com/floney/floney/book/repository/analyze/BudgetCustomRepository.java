package com.floney.floney.book.repository.analyze;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.response.BudgetYearResponse;
import com.floney.floney.common.domain.vo.DateDuration;

import java.util.List;

public interface BudgetCustomRepository {

    void deleteAllBy(Book book);

    List<BudgetYearResponse> findBudgetByYear(String bookKey, DateDuration duration);
}
