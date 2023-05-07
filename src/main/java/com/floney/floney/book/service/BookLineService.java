package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookLineExpense;
import com.floney.floney.book.dto.BookLineResponse;
import com.floney.floney.book.dto.CreateLineRequest;

import java.util.List;

public interface BookLineService {

    BookLineResponse createBookLine(CreateLineRequest request);

    List<BookLineExpense> allExpense(String bookKey, String date);
}
