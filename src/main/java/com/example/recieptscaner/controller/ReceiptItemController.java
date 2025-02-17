package com.example.recieptscaner.controller;

import com.example.recieptscaner.model.ReceiptItem;
import com.example.recieptscaner.service.ReceiptItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receipt-items")
@RequiredArgsConstructor
public class ReceiptItemController {

    private final ReceiptItemService receiptItemService;

    // Create a ReceiptItem
    @PostMapping
    public ResponseEntity<ReceiptItem> createReceiptItem(@RequestBody ReceiptItem receiptItem) {
        ReceiptItem savedItem = receiptItemService.createReceiptItem(receiptItem);
        return ResponseEntity.ok(savedItem);
    }

    // Get all ReceiptItems
    @GetMapping
    public ResponseEntity<List<ReceiptItem>> getAllReceiptItems() {
        return ResponseEntity.ok(receiptItemService.getAllReceiptItems());
    }

    // Get a ReceiptItem by ID
    @GetMapping("/{id}")
    public ResponseEntity<ReceiptItem> getReceiptItemById(@PathVariable Long id) {
        Optional<ReceiptItem> item = receiptItemService.getReceiptItemById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get ReceiptItems by Receipt ID
    @GetMapping("/receipt/{receiptId}")
    public ResponseEntity<List<ReceiptItem>> getReceiptItemsByReceiptId(@PathVariable Long receiptId) {
        return ResponseEntity.ok(receiptItemService.getReceiptItemsByReceiptId(receiptId));
    }

    // Get ReceiptItems by Category ID
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ReceiptItem>> getReceiptItemsByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(receiptItemService.getReceiptItemsByCategoryId(categoryId));
    }

    // Update a ReceiptItem
    @PutMapping("/{id}")
    public ResponseEntity<ReceiptItem> updateReceiptItem(@PathVariable Long id, @RequestBody ReceiptItem updatedItem) {
        try {
            ReceiptItem item = receiptItemService.updateReceiptItem(id, updatedItem);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a ReceiptItem
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceiptItem(@PathVariable Long id) {
        receiptItemService.deleteReceiptItem(id);
        return ResponseEntity.noContent().build();
    }
}
