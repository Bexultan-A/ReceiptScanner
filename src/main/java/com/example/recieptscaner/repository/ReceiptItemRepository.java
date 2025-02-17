package com.example.recieptscaner.repository;

import com.example.recieptscaner.model.ReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptItemRepository extends JpaRepository<ReceiptItem, Long> {
    List<ReceiptItem> findByReceiptReceiptId(Long receiptId);
    List<ReceiptItem> findByCategoryCategoryId(Long categoryId);
}
