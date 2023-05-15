package com.floney.floney.book.service;

import com.floney.floney.book.dto.*;

public interface BookLineService {

    BookLineResponse createBookLine(CreateLineRequest request);

    MonthLinesResponse showByMonth(String bookKey, String date);

    TotalDayLinesResponse showByDays(String bookKey, String date);
}
