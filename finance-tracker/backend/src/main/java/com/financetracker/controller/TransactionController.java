package com.financetracker.controller;

import com.financetracker.dto.TransactionDTO;
import com.financetracker.model.TransactionType;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<TransactionDTO.Response> create(@Valid @RequestBody TransactionDTO.Request request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO.Response> update(@PathVariable Long id,
                                                            @Valid @RequestBody TransactionDTO.Request request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        transactionService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO.Response> getOne(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getOne(userId, id));
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDTO.Response>> search(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long userId = securityUtils.getCurrentUserId();
        Page<TransactionDTO.Response> result = transactionService.search(
                userId, type, categoryId, startDate, endDate, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }
}
