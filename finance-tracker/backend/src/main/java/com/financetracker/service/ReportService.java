package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import com.financetracker.repository.TransactionRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Transactional(readOnly = true)
    public byte[] generateCsvReport(Long userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        List<Transaction> transactions = transactionRepository.findAllInRange(userId, ym.atDay(1), ym.atEndOfMonth());

        StringWriter stringWriter = new StringWriter();
        try (CSVWriter writer = new CSVWriter(stringWriter)) {
            writer.writeNext(new String[]{"Date", "Type", "Category", "Description", "Amount"});

            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;

            for (Transaction t : transactions) {
                writer.writeNext(new String[]{
                        t.getTransactionDate().format(DATE_FMT),
                        t.getType().name(),
                        t.getCategory().getName(),
                        t.getDescription() == null ? "" : t.getDescription(),
                        t.getAmount().toPlainString()
                });
                if (t.getType() == TransactionType.INCOME) {
                    totalIncome = totalIncome.add(t.getAmount());
                } else {
                    totalExpense = totalExpense.add(t.getAmount());
                }
            }

            writer.writeNext(new String[]{});
            writer.writeNext(new String[]{"", "", "", "Total Income", totalIncome.toPlainString()});
            writer.writeNext(new String[]{"", "", "", "Total Expense", totalExpense.toPlainString()});
            writer.writeNext(new String[]{"", "", "", "Net Balance", totalIncome.subtract(totalExpense).toPlainString()});
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV report", e);
        }

        return stringWriter.toString().getBytes();
    }

    @Transactional(readOnly = true)
    public byte[] generatePdfReport(Long userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        List<Transaction> transactions = transactionRepository.findAllInRange(userId, ym.atDay(1), ym.atEndOfMonth());
        String monthLabel = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + ym.getYear();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(0x1E, 0x32, 0x36));
            Font subtitleFont = new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(0x8F, 0xA6, 0xA3));
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            Font cellFont = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
            Font totalFont = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(0x1E, 0x32, 0x36));

            Paragraph title = new Paragraph("Monthly Finance Report", titleFont);
            title.setSpacingAfter(4);
            document.add(title);

            Paragraph subtitle = new Paragraph(monthLabel, subtitleFont);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            PdfPTable table = new PdfPTable(new float[]{2f, 1.3f, 2f, 3f, 1.6f});
            table.setWidthPercentage(100);

            String[] headers = {"Date", "Type", "Category", "Description", "Amount"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(0x1E, 0x32, 0x36));
                cell.setPadding(6);
                table.addCell(cell);
            }

            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;
            boolean alternate = false;

            for (Transaction t : transactions) {
                Color rowColor = alternate ? new Color(0xF2, 0xF6, 0xF5) : Color.WHITE;
                alternate = !alternate;

                addCell(table, t.getTransactionDate().format(DATE_FMT), cellFont, rowColor);
                addCell(table, t.getType().name(), cellFont, rowColor);
                addCell(table, t.getCategory().getName(), cellFont, rowColor);
                addCell(table, t.getDescription() == null ? "-" : t.getDescription(), cellFont, rowColor);
                addCell(table, formatMoney(t.getAmount()), cellFont, rowColor);

                if (t.getType() == TransactionType.INCOME) {
                    totalIncome = totalIncome.add(t.getAmount());
                } else {
                    totalExpense = totalExpense.add(t.getAmount());
                }
            }

            if (transactions.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("No transactions recorded this month.", cellFont));
                emptyCell.setColspan(5);
                emptyCell.setPadding(10);
                table.addCell(emptyCell);
            }

            document.add(table);

            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingAfter(10);
            document.add(spacer);

            BigDecimal net = totalIncome.subtract(totalExpense);
            document.add(new Paragraph("Total Income: " + formatMoney(totalIncome), totalFont));
            document.add(new Paragraph("Total Expense: " + formatMoney(totalExpense), totalFont));
            document.add(new Paragraph("Net Balance: " + formatMoney(net), totalFont));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }

        return baos.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, Color background) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        cell.setBackgroundColor(background);
        table.addCell(cell);
    }

    private String formatMoney(BigDecimal amount) {
        return "$" + amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }
}