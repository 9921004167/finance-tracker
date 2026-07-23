package com.financetracker.repository;

import com.financetracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user.id = :userId")
    List<Category> findAllVisibleToUser(@Param("userId") Long userId);

    Optional<Category> findByIdAndUserIsNullOrIdAndUserId(Long id1, Long id2, Long userId);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND (c.user IS NULL OR c.user.id = :userId)")
    Optional<Category> findByIdVisibleToUser(@Param("id") Long id, @Param("userId") Long userId);

    boolean existsByNameAndUserId(String name, Long userId);
}
