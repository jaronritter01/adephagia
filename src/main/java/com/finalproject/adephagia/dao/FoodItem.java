package com.finalproject.adephagia.dao;

import com.finalproject.adephagia.util.Unit;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;

@Data
@Entity
@Table(name = "food_items")
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(length = 2048)
    private String description;
    private boolean reusable;
    @Column(length = 2048)
    private String picUrl;
    private Unit.UNIT_TYPE unitType;
}
