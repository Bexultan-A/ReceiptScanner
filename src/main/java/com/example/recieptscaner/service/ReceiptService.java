package com.example.recieptscaner.service;

import com.example.recieptscaner.model.Receipt;
import com.example.recieptscaner.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    // Create a Receipt
    public Receipt createReceipt(Receipt receipt) {
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setUpdatedAt(LocalDateTime.now());
        return receiptRepository.save(receipt);
    }

    // Get all Receipts
    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    // Get a Receipt by ID
    public Optional<Receipt> getReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    // Get Receipts by User ID
    public List<Receipt> getReceiptsByUserId(Long userId) {
        return receiptRepository.findByUserUserId(userId);
    }

    // Get Receipts by Store ID
    public List<Receipt> getReceiptsByStoreId(Long storeId) {
        return receiptRepository.findByStoreStoreId(storeId);
    }

    // Update a Receipt
    public Receipt updateReceipt(Long id, Receipt updatedReceipt) {
        return receiptRepository.findById(id)
                .map(existingReceipt -> {
                    existingReceipt.setStore(updatedReceipt.getStore());
                    existingReceipt.setStoreName(updatedReceipt.getStoreName());
                    existingReceipt.setReceiptDate(updatedReceipt.getReceiptDate());
                    existingReceipt.setTotalAmount(updatedReceipt.getTotalAmount());
                    existingReceipt.setTaxAmount(updatedReceipt.getTaxAmount());
                    existingReceipt.setScannedImageUrl(updatedReceipt.getScannedImageUrl());
                    existingReceipt.setOcrText(updatedReceipt.getOcrText());
                    existingReceipt.setUpdatedAt(LocalDateTime.now());
                    return receiptRepository.save(existingReceipt);
                })
                .orElseThrow(() -> new RuntimeException("Receipt not found with ID: " + id));
    }

    // Delete a Receipt
    public void deleteReceipt(Long id) {
        receiptRepository.deleteById(id);
    }
}
