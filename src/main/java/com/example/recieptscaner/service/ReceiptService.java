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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.io.File;

@Service
public class ReceiptService {

    private String uploadDir = "src/main/resources/uploads";

    private final ReceiptRepository receiptRepository;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final ReceiptItemRepository receiptItemRepository;

    private String fastApiUrl = "http://localhost:8000/extract";

    public ReceiptService(ReceiptRepository receiptRepository,
                          RestTemplate restTemplate,
                          UserRepository userRepository,
                          StoreRepository storeRepository,
                          CategoryRepository categoryRepository,
                          ReceiptItemRepository receiptItemRepository) {
        this.receiptRepository = receiptRepository;
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.receiptItemRepository = receiptItemRepository;
    }

    public ReceiptDTO extractReceiptData(MultipartFile file) throws IOException {
        String filePath = saveFile(file);


        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("file", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Directly receive ReceiptDTO instead of mapping manually
        ResponseEntity<ReceiptDTO> response = restTemplate.postForEntity(fastApiUrl, requestEntity, ReceiptDTO.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to extract receipt data from image");
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        // Ensure the upload directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique file name to avoid conflicts
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // Save the file
        Files.copy(file.getInputStream(), filePath);

        // Return the file path or URL
        return filePath.toString();
    }



    @Transactional
    public String saveReceipt(ReceiptDTO receiptDTO) {
        User user = userRepository.findById(receiptDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Store store = receiptDTO.getStoreName() != null ? storeRepository.findByName(receiptDTO.getStoreName()) : null;

        Receipt receipt = Receipt.builder()
                .user(user)
                .store(store)
                .storeName(receiptDTO.getStoreName())
                .receiptDate(receiptDTO.getReceiptDate())
                .totalAmount(receiptDTO.getTotalAmount())
                .taxAmount(receiptDTO.getTaxAmount())
                .scannedImageUrl(receiptDTO.getScannedImageUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        receipt = receiptRepository.save(receipt);

        Receipt finalReceipt = receipt;
        List<ReceiptItem> receiptItems = receiptDTO.getReceiptItems().stream().map(itemDTO -> {
            Category category = categoryRepository.findByName(itemDTO.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            return ReceiptItem.builder()
                    .receipt(finalReceipt)
                    .description(itemDTO.getDescription())
                    .quantity(itemDTO.getQuantity())
                    .unitPrice(itemDTO.getUnitPrice())
                    .category(category)
                    .totalPrice(itemDTO.getTotalPrice())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }).toList();

        receipt.getReceiptItems().addAll(receiptItems);

        for (ReceiptItem receiptItem : receiptItems) {
            receiptItemRepository.save(receiptItem);
        }

        return "Successfully saved";
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
