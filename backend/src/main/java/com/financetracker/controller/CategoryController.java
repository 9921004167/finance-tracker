package com.financetracker.controller;

import com.financetracker.dto.CategoryDTO;
import com.financetracker.repository.UserRepository;
import com.financetracker.security.SecurityUtils;
import com.financetracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<CategoryDTO.Response>> getAll() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(categoryService.getAllForUser(userId));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO.Response> create(@Valid @RequestBody CategoryDTO.Request request) {
        Long userId = securityUtils.getCurrentUserId();
        var userRef = userRepository.getReferenceById(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(userId, userRef, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        categoryService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
