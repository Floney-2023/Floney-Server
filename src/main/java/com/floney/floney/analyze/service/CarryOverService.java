package com.floney.floney.analyze.service;

import com.floney.floney.book.dto.process.CarryOverInfo;
import com.floney.floney.book.dto.request.BookLineRequest;
import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.domain.entity.BookLine;

public interface CarryOverService {
    CarryOverInfo getCarryOverInfo(Book book, String date);

    void updateCarryOver(BookLineRequest request, BookLine savedBookLine);

    void createCarryOverByAddBookLine(BookLineRequest request, Book book);

    void deleteCarryOver(Long savedBookLineId);
}
