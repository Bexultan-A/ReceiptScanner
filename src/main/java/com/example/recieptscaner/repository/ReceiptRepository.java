package com.example.recieptscaner.repository;

import com.example.recieptscaner.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByUserUserId(Long userId);
    List<Receipt> findByStoreStoreId(Long storeId);
}
