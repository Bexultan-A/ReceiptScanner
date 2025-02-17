package com.example.recieptscaner.service;

import com.example.recieptscaner.model.ReceiptItem;
import com.example.recieptscaner.repository.ReceiptItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptItemService {

    private final ReceiptItemRepository receiptItemRepository;

    // Create a ReceiptItem
    public ReceiptItem createReceiptItem(ReceiptItem receiptItem) {
        receiptItem.setCreatedAt(LocalDateTime.now());
        receiptItem.setUpdatedAt(LocalDateTime.now());
        return receiptItemRepository.save(receiptItem);
    }

    // Get all ReceiptItems
    public List<ReceiptItem> getAllReceiptItems() {
        return receiptItemRepository.findAll();
    }

    // Get a ReceiptItem by ID
    public Optional<ReceiptItem> getReceiptItemById(Long id) {
        return receiptItemRepository.findById(id);
    }

    // Get ReceiptItems by Receipt ID
    public List<ReceiptItem> getReceiptItemsByReceiptId(Long receiptId) {
        return receiptItemRepository.findByReceiptReceiptId(receiptId);
    }

    // Get ReceiptItems by Category ID
    public List<ReceiptItem> getReceiptItemsByCategoryId(Long categoryId) {
        return receiptItemRepository.findByCategoryCategoryId(categoryId);
    }

    // Update a ReceiptItem
    public ReceiptItem updateReceiptItem(Long id, ReceiptItem updatedItem) {
        return receiptItemRepository.findById(id)
                .map(existingItem -> {
                    existingItem.setDescription(updatedItem.getDescription());
                    existingItem.setQuantity(updatedItem.getQuantity());
                    existingItem.setUnitPrice(updatedItem.getUnitPrice());
                    existingItem.setCategory(updatedItem.getCategory());
                    existingItem.setTotalPrice(updatedItem.getTotalPrice());
                    existingItem.setUpdatedAt(LocalDateTime.now());
                    return receiptItemRepository.save(existingItem);
                })
                .orElseThrow(() -> new RuntimeException("ReceiptItem not found with ID: " + id));
    }

    // Delete a ReceiptItem
    public void deleteReceiptItem(Long id) {
        receiptItemRepository.deleteById(id);
    }
}
