package com.example.recieptscaner.repository;

import com.example.recieptscaner.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
