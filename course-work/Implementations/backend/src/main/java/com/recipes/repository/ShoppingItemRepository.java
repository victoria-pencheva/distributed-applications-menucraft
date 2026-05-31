package com.recipes.repository;

import com.recipes.entity.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {

    List<ShoppingItem> findByUserUsernameOrderByCheckedAscCreatedAtAsc(String username);

    java.util.Optional<ShoppingItem> findFirstByUserUsernameAndIngredientIdAndCheckedFalse(String username, Long ingredientId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ShoppingItem s WHERE s.user.username = :username")
    void deleteByUserUsername(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM ShoppingItem s WHERE s.user.username = :username AND s.checked = true")
    void deleteCheckedByUserUsername(String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM ShoppingItem s WHERE s.user.username = :username AND s.checked = false")
    void deleteUncheckedByUserUsername(String username);
}
