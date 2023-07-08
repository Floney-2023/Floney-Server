package com.floney.floney.book.service;

import com.floney.floney.book.dto.*;

import java.util.List;

public interface BookLineService {

    BookLineResponse createBookLine(CreateLineRequest request);

    MonthLinesResponse showByMonth(String bookKey, String date);

    TotalDayLinesResponse showByDays(String bookKey, String date);

    void deleteAllLine(String bookKey);

    List<DayLines> allSettlement(AllSettlementsRequest allSettlements);


}
