package com.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal netBalance;
        private int month;
        private int year;
        private List<CategoryBreakdown> expenseByCategory;
        private List<CategoryBreakdown> incomeByCategory;
        private List<MonthlyTrend> monthlyTrend;
        private List<BudgetDTO.Response> budgetAlerts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBreakdown {
        private Long categoryId;
        private String categoryName;
        private String color;
        private BigDecimal amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyTrend {
        private String label; // e.g. "Feb 2026"
        private int month;
        private int year;
        private BigDecimal income;
        private BigDecimal expense;
    }
}
