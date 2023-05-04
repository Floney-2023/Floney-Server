package com.floney.floney.book.service;

import com.floney.floney.book.dto.BookLineResponse;
import com.floney.floney.book.dto.CreateLineRequest;

public interface BookLineService {

    BookLineResponse createBookLine(CreateLineRequest request);
}
