package com.financetracker.controller;

import com.financetracker.security.SecurityUtils;
import com.financetracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final SecurityUtils securityUtils;

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        Long userId = securityUtils.getCurrentUserId();
        int[] resolved = resolveMonthYear(month, year);
        byte[] csv = reportService.generateCsvReport(userId, resolved[0], resolved[1]);

        String filename = "finance-report-" + monthLabel(resolved[0], resolved[1]) + ".csv";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(csv);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        Long userId = securityUtils.getCurrentUserId();
        int[] resolved = resolveMonthYear(month, year);
        byte[] pdf = reportService.generatePdfReport(userId, resolved[0], resolved[1]);

        String filename = "finance-report-" + monthLabel(resolved[0], resolved[1]) + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(pdf);
    }

    private int[] resolveMonthYear(Integer month, Integer year) {
        LocalDate now = LocalDate.now();
        return new int[]{month != null ? month : now.getMonthValue(), year != null ? year : now.getYear()};
    }

    private String monthLabel(int month, int year) {
        return YearMonth.of(year, month).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase() + "-" + year;
    }
}
