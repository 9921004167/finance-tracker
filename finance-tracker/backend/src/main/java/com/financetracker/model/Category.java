package com.financetracker.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // whether this category is typically INCOME or EXPENSE

    @Column(length = 20)
    private String color; // hex color used for chart rendering, e.g. "#3ECF8E"

    @Column(length = 30)
    private String icon; // icon key used by the frontend, e.g. "groceries"

    /** Null user_id = a global/default category available to every user. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
