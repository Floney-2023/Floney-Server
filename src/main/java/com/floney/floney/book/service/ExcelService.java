package com.floney.floney.book.service;

import com.floney.floney.book.dto.request.ExcelDownloadRequest;
import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelService {

    Workbook createBookExcel(final String userEmail, final ExcelDownloadRequest bookKey);
}
