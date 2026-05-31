package com.recipes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "meal_plan_entries",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "date", "meal_type"})
)
@Getter
@Setter
public class MealPlanEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(nullable = false, columnDefinition = "integer not null default 1")
    private int servings = 1;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public MealPlanEntry() {}

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
