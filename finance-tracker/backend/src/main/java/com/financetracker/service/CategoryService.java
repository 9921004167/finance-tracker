package com.financetracker.service;

import com.financetracker.dto.CategoryDTO;
import com.financetracker.exception.DuplicateResourceException;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.Category;
import com.financetracker.model.TransactionType;
import com.financetracker.model.User;
import com.financetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private static final String[][] DEFAULT_EXPENSE_CATEGORIES = {
            {"Groceries", "#3ECF8E", "groceries"},
            {"Rent & Housing", "#5B8DEF", "housing"},
            {"Utilities", "#F5B942", "utilities"},
            {"Transportation", "#9B7EDE", "transport"},
            {"Dining Out", "#FF6B5C", "dining"},
            {"Entertainment", "#FF9F6B", "entertainment"},
            {"Healthcare", "#4FD1C5", "healthcare"},
            {"Shopping", "#F76FB0", "shopping"},
            {"Education", "#6EC1E4", "education"},
            {"Other Expense", "#8FA6A3", "other"}
    };

    private static final String[][] DEFAULT_INCOME_CATEGORIES = {
            {"Salary", "#3ECF8E", "salary"},
            {"Freelance", "#5B8DEF", "freelance"},
            {"Investments", "#F5B942", "investments"},
            {"Gifts", "#F76FB0", "gifts"},
            {"Other Income", "#8FA6A3", "other"}
    };

    @Transactional
    public void seedDefaultCategoriesForUser(User user) {
        // Default categories are global (user = null) and shared across all accounts,
        // so we only need to create them once. If they already exist, skip seeding.
        boolean alreadySeeded = !categoryRepository.findAllVisibleToUser(user.getId()).isEmpty();
        if (alreadySeeded) {
            return;
        }

        for (String[] c : DEFAULT_EXPENSE_CATEGORIES) {
            categoryRepository.save(Category.builder()
                    .name(c[0]).type(TransactionType.EXPENSE).color(c[1]).icon(c[2]).user(null).build());
        }
        for (String[] c : DEFAULT_INCOME_CATEGORIES) {
            categoryRepository.save(Category.builder()
                    .name(c[0]).type(TransactionType.INCOME).color(c[1]).icon(c[2]).user(null).build());
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO.Response> getAllForUser(Long userId) {
        return categoryRepository.findAllVisibleToUser(userId).stream()
                .map(c -> toResponse(c, userId))
                .toList();
    }

    @Transactional
    public CategoryDTO.Response create(Long userId, User userRef, CategoryDTO.Request request) {
        if (categoryRepository.existsByNameAndUserId(request.getName(), userId)) {
            throw new DuplicateResourceException("You already have a category named '" + request.getName() + "'");
        }
        Category category = Category.builder()
                .name(request.getName())
                .type(request.getType())
                .color(request.getColor() != null ? request.getColor() : "#3ECF8E")
                .icon(request.getIcon() != null ? request.getIcon() : "other")
                .user(userRef)
                .build();
        Category saved = categoryRepository.save(category);
        return toResponse(saved, userId);
    }

    @Transactional
    public void delete(Long userId, Long categoryId) {
        Category category = categoryRepository.findByIdVisibleToUser(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (category.getUser() == null) {
            throw new IllegalArgumentException("Default categories cannot be deleted");
        }
        categoryRepository.delete(category);
    }

    private CategoryDTO.Response toResponse(Category c, Long userId) {
        CategoryDTO.Response dto = new CategoryDTO.Response();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setType(c.getType());
        dto.setColor(c.getColor());
        dto.setIcon(c.getIcon());
        dto.setCustom(c.getUser() != null);
        return dto;
    }
}