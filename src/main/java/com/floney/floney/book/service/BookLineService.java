package com.floney.floney.book.service;

import com.floney.floney.book.domain.vo.MonthLinesResponse;
import com.floney.floney.book.dto.process.DayLines;
import com.floney.floney.book.dto.request.AllOutcomesRequest;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.dto.response.BookLineResponse;
import com.floney.floney.book.dto.response.TotalDayLinesResponse;

import java.util.List;

public interface BookLineService {

    BookLineResponse createBookLine(String currentUser, BookLineRequest request);

    MonthLinesResponse showByMonth(String bookKey, String date);

    TotalDayLinesResponse showByDays(String bookKey, String date);

    List<DayLines> allOutcomes(AllOutcomesRequest allSettlements);

    BookLineResponse changeLine(BookLineRequest request);

    void deleteLine(Long bookLineId);

    void deleteAllAfterBookLineByRepeat(long bookLineId);
}
