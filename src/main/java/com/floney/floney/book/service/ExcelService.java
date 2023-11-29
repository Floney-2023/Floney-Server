package com.floney.floney.book.service;

import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelService {

    Workbook createBookExcel(final String userEmail, final String bookKey);
}
