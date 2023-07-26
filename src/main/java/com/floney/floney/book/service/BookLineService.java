package com.floney.floney.book.service;

import com.floney.floney.book.dto.process.DayLines;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.request.CreateLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.MonthLinesResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;

import java.util.List;

public interface BookLineService {

    BookLineResponse createBookLine(String currentUser, CreateLineRequest request);

    MonthLinesResponse showByMonth(String bookKey, String date);

    TotalDayLinesResponse showByDays(String bookKey, String date);

    void deleteAllLine(String bookKey);

    List<DayLines> allOutcomes(AllOutcomesRequest allSettlements);


}
