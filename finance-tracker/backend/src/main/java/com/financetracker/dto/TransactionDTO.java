package com.financetracker.dto;

import com.financetracker.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDTO {

    /** Used for create/update requests. */
    @Data
    public static class Request {
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        private BigDecimal amount;

        @NotNull(message = "Type is required")
        private TransactionType type;

        private String description;

        @NotNull(message = "Transaction date is required")
        private LocalDate transactionDate;

        @NotNull(message = "Category is required")
        private Long categoryId;
    }

    /** Used for API responses. */
    @Data
    public static class Response {
        private Long id;
        private BigDecimal amount;
        private TransactionType type;
        private String description;
        private LocalDate transactionDate;
        private Long categoryId;
        private String categoryName;
        private String categoryColor;
        private String categoryIcon;
    }
}
