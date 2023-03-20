package com.finalproject.adephagia.repository;

import com.finalproject.adephagia.dao.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findFoodItemsByReusable(Boolean isResuable);
    Optional<FoodItem> findFoodItemsByName(String name);
}
