package com.finalproject.adephagia.dao;

import jakarta.persistence.*;
import lombok.Data;

//I'm guessing this is for finding or getting something related to finding new recipes, but I am not sure what

@Entity
@Table(name = "substitutions")
@Data
public class Substitution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private FoodItem missingItem;
}
