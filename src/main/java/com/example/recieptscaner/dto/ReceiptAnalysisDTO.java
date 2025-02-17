package com.example.recieptscaner.dto;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptAnalysisDTO {
    private Map<String, Double> topStores; // Store name -> Total spending
    private Map<String, Double> topCategories; // Category name -> Total spending
    private Map<String, Double> topReceiptItems; // Item description -> Total spending
    private Double totalSpending; // Total money spent in the week
}