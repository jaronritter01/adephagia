package com.finalproject.adephagia.repository;

import com.finalproject.adephagia.dao.Recipe;
import com.finalproject.adephagia.dao.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    Optional<List<RecipeItem>> findRecipeItemsByRecipe(Recipe recipe);
    Optional<List<RecipeItem>> deleteRecipeItemsByRecipe(Recipe recipe);
}
