package com.finalproject.adephagia.dto;

import lombok.Data;

import java.util.List;

@Data
public class InitialRequestItem {
    List<IngestionItem> meals;
}
