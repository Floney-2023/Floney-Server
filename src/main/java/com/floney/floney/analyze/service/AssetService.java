package com.floney.floney.analyze.service;

import com.floney.floney.book.domain.entity.Book;
import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.dto.request.BookLineRequest;

import java.time.LocalDate;
import java.util.Map;

public interface AssetService {

    Map<LocalDate, AssetInfo> getAssetInfo(Book book, String date);

    void createAssetBy(BookLineRequest request, Book book);

    void deleteAsset(Long bookLineId);
}
