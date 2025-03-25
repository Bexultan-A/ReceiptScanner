package com.example.recieptscaner.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptDTO {
    private String storeName;
    private LocalDate receiptDate;
    private Double totalAmount;
    private Double taxAmount;
    private String scannedImageUrl;
    private List<ReceiptItemDTO> receiptItems;
}
