package com.example.recieptscaner.controller;

import com.example.recieptscaner.model.Receipt;
import com.example.recieptscaner.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    // Create a Receipt
    @PostMapping
    public ResponseEntity<Receipt> createReceipt(@RequestBody Receipt receipt) {
        Receipt savedReceipt = receiptService.createReceipt(receipt);
        return ResponseEntity.ok(savedReceipt);
    }

    // Get all Receipts
    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }

    // Get a Receipt by ID
    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable Long id) {
        Optional<Receipt> receipt = receiptService.getReceiptById(id);
        return receipt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get Receipts by User ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Receipt>> getReceiptsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(receiptService.getReceiptsByUserId(userId));
    }

    // Get Receipts by Store ID
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Receipt>> getReceiptsByStoreId(@PathVariable Long storeId) {
        return ResponseEntity.ok(receiptService.getReceiptsByStoreId(storeId));
    }

    // Update a Receipt
    @PutMapping("/{id}")
    public ResponseEntity<Receipt> updateReceipt(@PathVariable Long id, @RequestBody Receipt updatedReceipt) {
        try {
            Receipt receipt = receiptService.updateReceipt(id, updatedReceipt);
            return ResponseEntity.ok(receipt);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a Receipt
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.noContent().build();
    }
}
