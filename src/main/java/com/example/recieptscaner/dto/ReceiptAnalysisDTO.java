package com.example.recieptscaner.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptAnalysisDTO {
    private Map<String, Double> topStores;
    private Map<String, Double> topCategories;
    private Map<String, Double> topReceiptItems;
    private Double totalSpending;

    private String mostExpensiveItem;
    private Double biggestPurchase;
    private Double smallestPurchase;
    private Integer totalTransactions;
    private List<String> popularCategories;
    private Map<LocalDate, Double> dailySpending;
    private Map<String, Double> averageCheckByCategory;
    private Map<String, Map<String, Double>> priceComparisonByStore;
}
