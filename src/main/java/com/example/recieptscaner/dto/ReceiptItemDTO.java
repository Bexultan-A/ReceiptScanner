package com.example.recieptscaner.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptItemDTO {
    private String description;
    private Double quantity;
    private Double unitPrice;
    private String categoryName;
    private Double totalPrice;
}
