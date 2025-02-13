package com.example.recieptscaner.service.interfaces;

import com.example.recieptscaner.model.Receipt;
import com.example.recieptscaner.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    public List<Receipt> getAllProducts() {
        return receiptRepository.findAll();
    }
}
