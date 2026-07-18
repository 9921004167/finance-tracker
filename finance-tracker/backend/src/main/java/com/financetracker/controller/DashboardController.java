package com.financetracker.controller;

import com.financetracker.dto.DashboardSummaryDTO;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final SecurityUtils securityUtils;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO.Summary> getSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {

        Long userId = securityUtils.getCurrentUserId();
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();

        return ResponseEntity.ok(dashboardService.getSummary(userId, m, y));
    }
}
