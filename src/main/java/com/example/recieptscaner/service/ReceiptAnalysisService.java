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

    public ReceiptAnalysisDTO analyzeReceiptsForWeek(Long userId, LocalDate startDate, LocalDate endDate) {
        // Fetch receipts for the user within the specified date range
        List<Receipt> receipts = receiptRepository.findByUserUserIdAndReceiptDateBetween(userId, startDate, endDate);

        // Calculate total spending
        Double totalSpending = receipts.stream()
                .mapToDouble(Receipt::getTotalAmount)
                .sum();

        // Calculate top stores
        Map<String, Double> topStores = receipts.stream()
                .collect(Collectors.groupingBy(
                        Receipt -> Receipt.getStore().getName(),
                        Collectors.summingDouble(Receipt::getTotalAmount)
                ))
                .entrySet().stream() // Process the entrySet of the resulting Map
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Calculate top categories
        Map<String, Double> topCategories = receipts.stream()
                .flatMap(receipt -> receipt.getReceiptItems().stream())
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

        // Calculate top receipt items
        Map<String, Double> topReceiptItems = receipts.stream()
                .flatMap(receipt -> receipt.getReceiptItems().stream())
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

        // Build and return the analysis result
        return ReceiptAnalysisDTO.builder()
                .topStores(topStores)
                .topCategories(topCategories)
                .topReceiptItems(topReceiptItems)
                .totalSpending(totalSpending)
                .build();
    }
}