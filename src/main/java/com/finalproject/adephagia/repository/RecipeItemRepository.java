package com.finalproject.adephagia.repository;

import com.finalproject.adephagia.dao.Recipe;
import com.finalproject.adephagia.dao.RecipeItem;
import com.finalproject.adephagia.dto.UnitAggRows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    Optional<List<RecipeItem>> findRecipeItemsByRecipe(Recipe recipe);
    Optional<List<RecipeItem>> deleteRecipeItemsByRecipe(Recipe recipe);

    List<RecipeItem> findByFoodItemId(long id);

    @Query(
            value = "SELECT measurement_unit as unitType, count(measurement_unit)" +
                    "FROM recipe_items WHERE food_item_id=?1 " +
                    "GROUP BY measurement_unit",
            nativeQuery = true
    )
    List<UnitAggRows> getUniqueUnitsAndCounts(long foodItemId);
}
