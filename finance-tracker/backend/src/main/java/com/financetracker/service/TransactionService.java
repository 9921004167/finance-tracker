package com.financetracker.service;

import com.financetracker.dto.TransactionDTO;
import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import com.financetracker.model.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionDTO.Response create(Long userId, TransactionDTO.Request request) {
        User user = userRepository.getReferenceById(userId);
        Category category = categoryRepository.findByIdVisibleToUser(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .description(request.getDescription())
                .transactionDate(request.getTransactionDate())
                .category(category)
                .user(user)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    @Transactional
    public TransactionDTO.Response update(Long userId, Long transactionId, TransactionDTO.Request request) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        Category category = categoryRepository.findByIdVisibleToUser(request.getCategoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setCategory(category);

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public void delete(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionDTO.Response getOne(Long userId, Long transactionId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    @Transactional(readOnly = true)
    public Page<TransactionDTO.Response> search(Long userId, TransactionType type, Long categoryId,
                                                LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return transactionRepository.search(userId, type, categoryId, startDate, endDate, pageable)
                .map(this::toResponse);
    }

    private TransactionDTO.Response toResponse(Transaction t) {
        TransactionDTO.Response dto = new TransactionDTO.Response();
        dto.setId(t.getId());
        dto.setAmount(t.getAmount());
        dto.setType(t.getType());
        dto.setDescription(t.getDescription());
        dto.setTransactionDate(t.getTransactionDate());
        dto.setCategoryId(t.getCategory().getId());
        dto.setCategoryName(t.getCategory().getName());
        dto.setCategoryColor(t.getCategory().getColor());
        dto.setCategoryIcon(t.getCategory().getIcon());
        return dto;
    }
}