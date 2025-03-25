package com.example.recieptscaner.service;

import com.example.recieptscaner.dto.ReceiptDTO;
import com.example.recieptscaner.model.*;
import com.example.recieptscaner.repository.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ReceiptItemRepository receiptItemRepository;

    private String fastApiUrl = "http://localhost:8000/extract";

    public ReceiptService(ReceiptRepository receiptRepository,
                          UserRepository userRepository,
                          StoreRepository storeRepository,
                          CategoryRepository categoryRepository,
                          ReceiptItemRepository receiptItemRepository) {
        this.receiptRepository = receiptRepository;
        this.restTemplate = new RestTemplate();
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.receiptItemRepository = receiptItemRepository;
    }

    @Transactional
    public Long createBlankReceipt(Long userId, String scannedImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Receipt receipt = Receipt.builder()
                .user(user)
                .scannedImageUrl(scannedImageUrl)
                .status("UNKNOWN") // Initial status
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        receipt = receiptRepository.save(receipt);
        return receipt.getReceiptId(); // Return the ID of the blank receipt
    }


    public void processImage(Long receiptId, byte[] fileBytes) throws IOException {
        // Convert byte array to Base64
        String base64Image = Base64.getEncoder().encodeToString(fileBytes);


        // Prepare JSON payload
        Map<String, Object> body = new HashMap<>();
        body.put("receiptId", receiptId);
        body.put("fileBase64", base64Image);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send the image to FastAPI
        ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Update the receipt status to "PROCESSING"
            Receipt receipt = receiptRepository.findById(receiptId)
                    .orElseThrow(() -> new RuntimeException("Receipt not found"));
            receipt.setStatus("PROCESSING");
            receiptRepository.save(receipt);
        } else {
            throw new RuntimeException("Failed to process image");
        }
    }

    @Transactional
    public void updateReceiptWithData(Long receiptId, ReceiptDTO receiptDTO) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new RuntimeException("Receipt not found"));

        Store store = storeRepository.findByName(receiptDTO.getStoreName())
                .orElseThrow(() -> new RuntimeException("Store not found"));

        receipt.setStore(store);
        receipt.setReceiptDate(receiptDTO.getReceiptDate());
        receipt.setTotalAmount(receiptDTO.getTotalAmount());
        receipt.setTaxAmount(receiptDTO.getTaxAmount());
        receipt.setStatus("COMPLETED"); // Update status to "COMPLETED"

        List<ReceiptItem> receiptItems = receiptDTO.getReceiptItems().stream().map(itemDTO -> {
            Category category = categoryRepository.findByName(itemDTO.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            return ReceiptItem.builder()
                    .receipt(receipt)
                    .description(itemDTO.getDescription())
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(itemDTO.getUnitPrice())
                    .category(category)
                    .totalPrice(itemDTO.getTotalPrice())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }).toList();

        receipt.getReceiptItems().clear();
        receipt.getReceiptItems().addAll(receiptItems);

        for (ReceiptItem receiptItem : receiptItems) {
            receiptItemRepository.save(receiptItem);
        }

        receiptRepository.save(receipt);
    }


    public String saveFile(byte[] fileBytes) throws IOException {
        // Get the absolute path to the `uploads` directory
        Path uploadDir = Paths.get("src/main/resources/uploads").toAbsolutePath();

        // Ensure the directory exists
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Generate a unique filename
        String fileName = UUID.randomUUID() + ".jpg";
        Path filePath = uploadDir.resolve(fileName);

        // Write the file to the directory
        Files.write(filePath, fileBytes, StandardOpenOption.CREATE);

        return filePath.toString();
    }




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
