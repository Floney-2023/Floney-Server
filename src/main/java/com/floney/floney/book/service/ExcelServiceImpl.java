package com.floney.floney.book.service;

import com.floney.floney.book.dto.constant.CategoryEnum;
import com.floney.floney.book.entity.BookLine;
import com.floney.floney.book.repository.BookLineRepository;
import com.floney.floney.book.repository.BookUserRepository;
import com.floney.floney.common.exception.book.NotFoundBookUserException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    private final BookUserRepository bookUserRepository;
    private final BookLineRepository bookLineRepository;

    @Override
    public Workbook createBookExcel(final String userEmail, final String bookKey) {
        validateBookUser(userEmail, bookKey);

        final List<BookLine> bookLines = bookLineRepository.findAllByBook(bookKey);
        final Workbook workbook = new XSSFWorkbook();
        final Sheet sheet = workbook.createSheet();

        final List<String> headers = List.of("사용자", "날짜", "수입/지출", "금액", "화폐", "자산", "분류", "내용");
        final Row headerRow = sheet.createRow(0);
        final CellStyle headerStyle = createHeaderStyle(workbook);
        final CellStyle cellStyle = createBorderStyle(workbook);

        setWidthOfCells(bookLines, sheet);

        for (int i = 0; i < headers.size(); i++) {
            final Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers.get(i));
            headerCell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < bookLines.size(); i++) {
            final Row row = sheet.createRow(i + 1);
            final BookLine bookLine = bookLines.get(i);

            int cellIdx = 0;
            final Cell writerCell = row.createCell(cellIdx++);
            writerCell.setCellValue(bookLine.getWriter());
            writerCell.setCellStyle(cellStyle);

            final Cell dateCell = row.createCell(cellIdx++);
            dateCell.setCellValue(bookLine.getLineDate());
            dateCell.setCellStyle(cellStyle);
            final CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(getDataFormat(workbook));
            dateCell.setCellStyle(dateStyle);

            final Cell flowCell = row.createCell(cellIdx++);
            flowCell.setCellValue(bookLine.getTargetCategory(CategoryEnum.FLOW));
            flowCell.setCellStyle(cellStyle);

            final Cell moneyCell = row.createCell(cellIdx++);
            moneyCell.setCellValue(bookLine.getMoney());
            moneyCell.setCellStyle(cellStyle);

            final Cell currencyCell = row.createCell(cellIdx++);
            currencyCell.setCellValue(bookLine.getBook().getCurrency());
            currencyCell.setCellStyle(cellStyle);

            final Cell assetCell = row.createCell(cellIdx++);
            assetCell.setCellValue(bookLine.getTargetCategory(CategoryEnum.ASSET));
            assetCell.setCellStyle(cellStyle);

            final Cell flowLineCell = row.createCell(cellIdx++);
            flowLineCell.setCellValue(bookLine.getTargetCategory(CategoryEnum.FLOW_LINE));
            flowLineCell.setCellStyle(cellStyle);

            final Cell descriptionCell = row.createCell(cellIdx);
            descriptionCell.setCellValue(bookLine.getDescription());
            descriptionCell.setCellStyle(cellStyle);
        }

        return workbook;
    }

    private short getDataFormat(final Workbook workbook) {
        return workbook.getCreationHelper()
                .createDataFormat()
                .getFormat("yyyy-mm-dd");
    }

    private static void setWidthOfCells(final List<BookLine> bookLines, final Sheet sheet) {
        int maxLengthOfWriter = 10;
        int maxLengthOfMoney = 5;
        int maxLengthOfDescription = 15;

        for (final BookLine bookLine : bookLines) {
            maxLengthOfWriter = Math.max(maxLengthOfWriter, bookLine.getWriter().length());
            maxLengthOfMoney = Math.max(maxLengthOfMoney, String.valueOf(bookLine.getMoney()).length());
            maxLengthOfDescription = Math.max(maxLengthOfDescription, bookLine.getDescription().length());
        }

        sheet.setColumnWidth(0, 10 + 256 * maxLengthOfWriter * 2);
        sheet.setColumnWidth(1, 10 + 256 * 15);
        sheet.setColumnWidth(3, 10 + 256 * maxLengthOfMoney * 2);
        sheet.setColumnWidth(7, 10 + 256 * maxLengthOfDescription * 2);
    }

    private CellStyle createBorderStyle(final Workbook workbook) {
        final CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setAlignment(HorizontalAlignment.LEFT);
        return borderStyle;
    }

    private CellStyle createHeaderStyle(final Workbook workbook) {
        final CellStyle headerStyle = workbook.createCellStyle();
        final XSSFColor xssfColor = new XSSFColor();
        final String colorHex = "31C690";

        xssfColor.setARGBHex(colorHex);
        headerStyle.setFillForegroundColor(xssfColor);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        return headerStyle;
    }

    private void validateBookUser(final String userEmail, final String bookKey) {
        if (!bookUserRepository.existsByUserEmailAndBookKey(userEmail, bookKey)) {
            throw new NotFoundBookUserException(bookKey, userEmail);
        }
    }
}
