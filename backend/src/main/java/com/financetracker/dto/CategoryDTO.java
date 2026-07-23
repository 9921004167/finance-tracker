package com.financetracker.dto;

import com.financetracker.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class CategoryDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Name is required")
        private String name;

        @NotNull(message = "Type is required")
        private TransactionType type;

        private String color;
        private String icon;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private TransactionType type;
        private String color;
        private String icon;
        private boolean custom; // true if user-created, false if a global default
    }
}
