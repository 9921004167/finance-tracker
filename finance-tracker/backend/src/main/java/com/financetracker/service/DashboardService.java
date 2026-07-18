package com.financetracker.service;

import com.financetracker.dto.DashboardSummaryDTO;
import com.financetracker.model.TransactionType;
import com.financetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;

    @Transactional(readOnly = true)
    public DashboardSummaryDTO.Summary getSummary(Long userId, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal totalIncome = transactionRepository.sumByUserAndTypeAndDateRange(
                userId, TransactionType.INCOME, start, end);
        BigDecimal totalExpense = transactionRepository.sumByUserAndTypeAndDateRange(
                userId, TransactionType.EXPENSE, start, end);

        List<DashboardSummaryDTO.CategoryBreakdown> expenseByCategory =
                buildBreakdown(userId, TransactionType.EXPENSE, start, end);
        List<DashboardSummaryDTO.CategoryBreakdown> incomeByCategory =
                buildBreakdown(userId, TransactionType.INCOME, start, end);

        List<DashboardSummaryDTO.MonthlyTrend> trend = buildTrend(userId, ym);

        return DashboardSummaryDTO.Summary.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(totalIncome.subtract(totalExpense))
                .month(month)
                .year(year)
                .expenseByCategory(expenseByCategory)
                .incomeByCategory(incomeByCategory)
                .monthlyTrend(trend)
                .budgetAlerts(budgetService.getOverBudgetAlerts(userId, month, year))
                .build();
    }

    private List<DashboardSummaryDTO.CategoryBreakdown> buildBreakdown(
            Long userId, TransactionType type, LocalDate start, LocalDate end) {
        List<Object[]> rows = transactionRepository.sumGroupedByCategory(userId, type, start, end);
        List<DashboardSummaryDTO.CategoryBreakdown> result = new ArrayList<>();
        for (Object[] row : rows) {
            result.add(DashboardSummaryDTO.CategoryBreakdown.builder()
                    .categoryId((Long) row[0])
                    .categoryName((String) row[1])
                    .color((String) row[2])
                    .amount((BigDecimal) row[3])
                    .build());
        }
        return result;
    }

    /** Builds a 6-month trailing trend (income vs expense) ending at the requested month. */
    private List<DashboardSummaryDTO.MonthlyTrend> buildTrend(Long userId, YearMonth current) {
        List<DashboardSummaryDTO.MonthlyTrend> trend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            BigDecimal income = transactionRepository.sumByUserAndTypeAndDateRange(
                    userId, TransactionType.INCOME, start, end);
            BigDecimal expense = transactionRepository.sumByUserAndTypeAndDateRange(
                    userId, TransactionType.EXPENSE, start, end);

            String label = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ym.getYear();

            trend.add(DashboardSummaryDTO.MonthlyTrend.builder()
                    .label(label)
                    .month(ym.getMonthValue())
                    .year(ym.getYear())
                    .income(income)
                    .expense(expense)
                    .build());
        }
        return trend;
    }
}