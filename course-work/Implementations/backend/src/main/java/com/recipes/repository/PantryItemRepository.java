package com.recipes.repository;

import com.recipes.entity.PantryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PantryItemRepository extends JpaRepository<PantryItem, Long> {

    List<PantryItem> findByUserUsername(String username);

    Optional<PantryItem> findByUserUsernameAndIngredientId(String username, Long ingredientId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PantryItem p WHERE p.user.username = :username")
    void deleteByUserUsername(String username);
}
