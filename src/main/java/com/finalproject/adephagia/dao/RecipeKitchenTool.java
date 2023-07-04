package com.finalproject.adephagia.dao;

import jakarta.persistence.*;
import lombok.Data;

//This one doesn't seem to be used, is it for later or something

@Entity
@Table(name = "recipe_kitchen_tools")
@Data
public class RecipeKitchenTool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private Recipe recipe;
    @ManyToOne
    private KitchenTool tool;
    private Integer quantity;
}
