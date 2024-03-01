package com.floney.floney.analyze.service;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;
import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.request.BookLineRequest;

public interface CarryOverService {
    CarryOverInfo getCarryOverInfo(Book book, String date);

    void updateCarryOver(BookLineRequest request, BookLine savedBookLine);

    void createCarryOverByAddBookLine(BookLine bookLine);

    void deleteCarryOver(Long savedBookLineId);
}
