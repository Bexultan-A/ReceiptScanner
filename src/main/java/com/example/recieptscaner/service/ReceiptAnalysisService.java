package com.example.recieptscaner.service;

import com.example.recieptscaner.dto.ReceiptAnalysisDTO;
import com.example.recieptscaner.model.Receipt;
import com.example.recieptscaner.model.ReceiptItem;
import com.example.recieptscaner.repository.ReceiptItemRepository;
import com.example.recieptscaner.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReceiptAnalysisService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;

    public ReceiptAnalysisService(ReceiptRepository receiptRepository, ReceiptItemRepository receiptItemRepository) {
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
    }

    public ReceiptAnalysisDTO analyzeReceipts(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Receipt> receipts = receiptRepository.findByUserUserIdAndReceiptDateBetween(userId, startDate, endDate);
        List<ReceiptItem> allItems = receipts.stream()
                .flatMap(r -> r.getReceiptItems().stream())
                .collect(Collectors.toList());

        Double totalSpending = receipts.stream()
                .mapToDouble(Receipt::getTotalAmount)
                .sum();

        Map<String, Double> topStores = receipts.stream()
                .collect(Collectors.groupingBy(
                        receipt -> receipt.getStore().getName(),
                        Collectors.summingDouble(Receipt::getTotalAmount)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        Map<String, Double> topCategories = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory().getName(),
                        Collectors.summingDouble(ReceiptItem::getTotalPrice)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        Map<String, Double> topReceiptItems = allItems.stream()
                .collect(Collectors.groupingBy(
                        ReceiptItem::getDescription,
                        Collectors.summingDouble(ReceiptItem::getTotalPrice)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        String mostExpensiveItem = topReceiptItems.entrySet().stream()
                .findFirst().map(Map.Entry::getKey).orElse(null);

        Double biggestPurchase = receipts.stream()
                .mapToDouble(Receipt::getTotalAmount)
                .max().orElse(0.0);

        Double smallestPurchase = receipts.stream()
                .mapToDouble(Receipt::getTotalAmount)
                .min().orElse(0.0);

        Integer totalTransactions = receipts.size();

        List<String> popularCategories = topCategories.keySet().stream()
                .limit(3)
                .toList();

        Map<LocalDate, Double> dailySpending = receipts.stream()
                .collect(Collectors.groupingBy(
                        Receipt::getReceiptDate,
                        Collectors.summingDouble(Receipt::getTotalAmount)
                ));

        Map<String, Double> averageCheckByCategory = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory().getName(),
                        Collectors.averagingDouble(ReceiptItem::getTotalPrice)
                ));

        Map<String, Map<String, Double>> priceComparisonByStore = allItems.stream()
                .collect(Collectors.groupingBy(
                        ReceiptItem::getDescription,
                        Collectors.groupingBy(
                                item -> item.getReceipt().getStore().getName(),
                                Collectors.averagingDouble(ReceiptItem::getUnitPrice)
                        )
                ));

        return ReceiptAnalysisDTO.builder()
                .topStores(topStores)
                .topCategories(topCategories)
                .topReceiptItems(topReceiptItems)
                .totalSpending(totalSpending)
                .mostExpensiveItem(mostExpensiveItem)
                .biggestPurchase(biggestPurchase)
                .smallestPurchase(smallestPurchase)
                .totalTransactions(totalTransactions)
                .popularCategories(popularCategories)
                .dailySpending(dailySpending)
                .averageCheckByCategory(averageCheckByCategory)
                .priceComparisonByStore(priceComparisonByStore)
                .build();
    }
}
