package com.example.recieptscaner.controller;

import com.example.recieptscaner.model.Receipt;
import com.example.recieptscaner.service.interfaces.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @GetMapping
    public List<Receipt> getAllProducts() {
        return receiptService.getAllProducts();
    }

}
