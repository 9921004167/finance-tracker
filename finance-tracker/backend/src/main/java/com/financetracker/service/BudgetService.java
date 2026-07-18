package com.financetracker.service;

import com.financetracker.dto.BudgetDTO;
import com.financetracker.exception.DuplicateResourceException;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.Budget;
import com.financetracker.model.Category;
import com.financetracker.model.TransactionType;
import com.financetracker.model.User;
import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public BudgetDTO.Response create(Long userId, BudgetDTO.Request request) {
        Category category = categoryRepository.findByIdVisibleToUser(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        budgetRepository.findByUserIdAndCategoryIdAndMonthAndYear(
                userId, request.getCategoryId(), request.getMonth(), request.getYear()
        ).ifPresent(b -> {
            throw new DuplicateResourceException(
                    "A budget for this category already exists for " + request.getMonth() + "/" + request.getYear());
        });

        User user = userRepository.getReferenceById(userId);

        Budget budget = Budget.builder()
                .limitAmount(request.getLimitAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .category(category)
                .user(user)
                .build();

        Budget saved = budgetRepository.save(budget);
        return toResponse(saved);
    }

    @Transactional
    public BudgetDTO.Response update(Long userId, Long budgetId, BudgetDTO.Request request) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        Category category = categoryRepository.findByIdVisibleToUser(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        budget.setLimitAmount(request.getLimitAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setCategory(category);

        return toResponse(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        budgetRepository.delete(budget);
    }

    @Transactional(readOnly = true)
    public List<BudgetDTO.Response> getForMonth(Long userId, int month, int year) {
        return budgetRepository.findByUserIdAndMonthAndYear(userId, month, year).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BudgetDTO.Response> getAllForUser(Long userId) {
        return budgetRepository.findAllByUser(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    /** Budgets that are at or over their limit for the given month - used for dashboard alerts. */
    @Transactional(readOnly = true)
    public List<BudgetDTO.Response> getOverBudgetAlerts(Long userId, int month, int year) {
        return getForMonth(userId, month, year).stream()
                .filter(BudgetDTO.Response::isOverBudget)
                .toList();
    }

    private BudgetDTO.Response toResponse(Budget budget) {
        YearMonth ym = YearMonth.of(budget.getYear(), budget.getMonth());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal spent = transactionRepository.sumGroupedByCategory(
                        budget.getUser().getId(), TransactionType.EXPENSE, start, end).stream()
                .filter(row -> row[0].equals(budget.getCategory().getId()))
                .map(row -> (BigDecimal) row[3])
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        double percentUsed = budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0
                ? spent.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100
                : 0;

        BudgetDTO.Response dto = new BudgetDTO.Response();
        dto.setId(budget.getId());
        dto.setCategoryId(budget.getCategory().getId());
        dto.setCategoryName(budget.getCategory().getName());
        dto.setCategoryColor(budget.getCategory().getColor());
        dto.setLimitAmount(budget.getLimitAmount());
        dto.setSpentAmount(spent);
        dto.setRemainingAmount(remaining);
        dto.setPercentUsed(Math.round(percentUsed * 10) / 10.0);
        dto.setOverBudget(spent.compareTo(budget.getLimitAmount()) > 0);
        dto.setMonth(budget.getMonth());
        dto.setYear(budget.getYear());
        return dto;
    }
}