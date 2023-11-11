package com.floney.floney.analyze.service;

import com.floney.floney.book.dto.process.AssetInfo;
import com.floney.floney.book.dto.request.ChangeBookLineRequest;
import com.floney.floney.book.entity.Book;
import com.floney.floney.book.entity.BookLine;

import java.time.LocalDate;
import java.util.Map;

public interface AssetService {
    Map<LocalDate, AssetInfo> getAssetInfo(Book book, String date);

    void updateAsset(ChangeBookLineRequest request, BookLine savedBookLine);

    void createAssetBy(ChangeBookLineRequest request, Book book);

    void deleteAsset(BookLine savedBookLine);
}
