package com.financetracker.repository;

import com.financetracker.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, Integer month, Integer year);

    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId ORDER BY b.year DESC, b.month DESC")
    List<Budget> findAllByUser(@Param("userId") Long userId);
}
