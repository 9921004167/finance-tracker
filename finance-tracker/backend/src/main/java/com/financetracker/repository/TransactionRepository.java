package com.financetracker.repository;

import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    Page<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);

    @Query("""
           SELECT t FROM Transaction t
           WHERE t.user.id = :userId
           AND (:type IS NULL OR t.type = :type)
           AND (:categoryId IS NULL OR t.category.id = :categoryId)
           AND (:startDate IS NULL OR t.transactionDate >= :startDate)
           AND (:endDate IS NULL OR t.transactionDate <= :endDate)
           ORDER BY t.transactionDate DESC, t.id DESC
           """)
    Page<Transaction> search(@Param("userId") Long userId,
                              @Param("type") TransactionType type,
                              @Param("categoryId") Long categoryId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              Pageable pageable);

    @Query("""
           SELECT t FROM Transaction t
           WHERE t.user.id = :userId
           AND t.transactionDate BETWEEN :startDate AND :endDate
           ORDER BY t.transactionDate ASC
           """)
    List<Transaction> findAllInRange(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    @Query("""
           SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
           WHERE t.user.id = :userId AND t.type = :type
           AND t.transactionDate BETWEEN :startDate AND :endDate
           """)
    BigDecimal sumByUserAndTypeAndDateRange(@Param("userId") Long userId,
                                             @Param("type") TransactionType type,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    @Query("""
           SELECT t.category.id, t.category.name, t.category.color, COALESCE(SUM(t.amount), 0)
           FROM Transaction t
           WHERE t.user.id = :userId AND t.type = :type
           AND t.transactionDate BETWEEN :startDate AND :endDate
           GROUP BY t.category.id, t.category.name, t.category.color
           """)
    List<Object[]> sumGroupedByCategory(@Param("userId") Long userId,
                                         @Param("type") TransactionType type,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
