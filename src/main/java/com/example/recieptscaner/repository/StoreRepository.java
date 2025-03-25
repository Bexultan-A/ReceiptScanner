package com.example.recieptscaner.repository;

import com.example.recieptscaner.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByCity(String city);
    List<Store> findByCountry(String country);
    Optional<Store> findByName(String store);
}
