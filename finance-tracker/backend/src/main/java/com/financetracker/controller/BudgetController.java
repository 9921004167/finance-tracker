package com.financetracker.controller;

import com.financetracker.dto.BudgetDTO;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<BudgetDTO.Response> create(@Valid @RequestBody BudgetDTO.Request request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.create(userId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO.Response> update(@PathVariable Long id,
                                                       @Valid @RequestBody BudgetDTO.Request request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        budgetService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<BudgetDTO.Response>> getForMonth(
            @RequestParam int month, @RequestParam int year) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getForMonth(userId, month, year));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BudgetDTO.Response>> getAll() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(budgetService.getAllForUser(userId));
    }
}
