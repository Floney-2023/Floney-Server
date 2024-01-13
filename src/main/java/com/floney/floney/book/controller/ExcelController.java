package com.floney.floney.book.controller;

import com.floney.floney.book.dto.request.ExcelDownloadRequest;
import com.floney.floney.book.service.ExcelService;
import com.floney.floney.common.exception.book.ExcelMakingException;
import com.floney.floney.user.dto.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/books/excel")
@RequiredArgsConstructor
public class ExcelController {

    public static final String FILENAME = URLEncoder.encode("[Floney] 가계부 엑셀 파일", StandardCharsets.UTF_8);

    private final ExcelService excelService;

    @GetMapping
    public void download(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @RequestBody ExcelDownloadRequest downloadRequest,
                         HttpServletResponse response) {

        try (final Workbook bookExcel = excelService.createBookExcel(userDetails.getUsername(), downloadRequest)) {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + FILENAME + ".xlsx");

            bookExcel.write(response.getOutputStream());
        } catch (final IOException exception) {
            throw new ExcelMakingException(exception.getLocalizedMessage());
        }
    }
}
