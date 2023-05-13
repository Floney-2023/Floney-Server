package com.floney.floney.book.service;

import com.floney.floney.book.dto.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookLineService {

    BookLineResponse createBookLine(CreateLineRequest request);

    CalendarLinesResponse showByCalendars(String bookKey, String date);

    List<DayLinesResponse> showByDays(String bookKey, String date);
}
