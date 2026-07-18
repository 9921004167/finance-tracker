package com.financetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

public class BudgetDTO {

    @Data
    public static class Request {
        @NotNull(message = "Category is required")
        private Long categoryId;

        @NotNull(message = "Limit amount is required")
        @DecimalMin(value = "0.01", message = "Limit must be greater than zero")
        private BigDecimal limitAmount;

        @NotNull @Min(1) @Max(12)
        private Integer month;

        @NotNull @Min(2000)
        private Integer year;
    }

    @Data
    public static class Response {
        private Long id;
        private Long categoryId;
        private String categoryName;
        private String categoryColor;
        private BigDecimal limitAmount;
        private BigDecimal spentAmount;
        private BigDecimal remainingAmount;
        private double percentUsed;
        private boolean overBudget;
        private Integer month;
        private Integer year;
    }
}
