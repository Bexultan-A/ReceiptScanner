package com.example.recieptscaner.controller;

import com.example.recieptscaner.dto.ReceiptAnalysisDTO;
import com.example.recieptscaner.dto.ReceiptDTO;
import com.example.recieptscaner.dto.ReceiptUploadRequest;
import com.example.recieptscaner.model.Receipt;
import com.example.recieptscaner.service.ReceiptService;
import com.example.recieptscaner.service.ReceiptAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Base64;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReceiptController {

    private final ReceiptService receiptService;
    private final ReceiptAnalysisService receiptAnalysisService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadReceipt(@RequestBody ReceiptUploadRequest request) {
        try {

            byte[] decodedBytes = Base64.getDecoder().decode(request.getFileBase64());

            // Step 1: Create and save a blank receipt
            String filePath = receiptService.saveFile(decodedBytes);
            Long receiptId = receiptService.createBlankReceipt(request.getUserId(), filePath);

            // Step 2: Send the image to FastAPI and update the receipt status
//            receiptService.processImage(receiptId, decodedBytes);

            return ResponseEntity.ok("Receipt is being processed. Receipt ID: " + receiptId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing the image: " + e.getMessage());
        }
    }


    @PostMapping("/update")
    public ResponseEntity<String> updateReceipt(@RequestBody ReceiptDTO receiptDTO, @RequestParam Long receiptId) {
        try {
            // Step 3: Update the blank receipt with the extracted data
            receiptService.updateReceiptWithData(receiptId, receiptDTO);
            return ResponseEntity.ok("Receipt updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating the receipt: " + e.getMessage());
        }
    }


    @GetMapping("/analyze")
    public ReceiptAnalysisDTO analyzeReceipts(
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String period // daily, weekly, monthly, yearly
    ) {
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : LocalDate.now();
        LocalDate start;

        if (startDate != null) {
            start = LocalDate.parse(startDate);
        } else if (period != null) {
            switch (period.toLowerCase()) {
                case "daily" -> start = end;
                case "weekly" -> start = end.minusWeeks(1);
                case "monthly" -> start = end.minusMonths(1);
                case "yearly" -> start = end.minusYears(1);
                default -> throw new IllegalArgumentException("Invalid period type: " + period);
            }
        } else {
            // default to last 30 days
            start = end.minusDays(30);
        }

        return receiptAnalysisService.analyzeReceipts(userId, start, end);
    }



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
