package com.example.recieptscaner.dto;

import lombok.Data;

@Data
public class ReceiptUploadRequest {
    private Long userId;
    private String fileBase64;
}
