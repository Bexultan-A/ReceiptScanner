package com.example.recieptscaner.service;

import com.example.recieptscaner.model.Store;
import com.example.recieptscaner.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    // Create a Store
    public Store createStore(Store store) {
        store.setCreatedAt(LocalDateTime.now());
        store.setUpdatedAt(LocalDateTime.now());
        return storeRepository.save(store);
    }

    // Get all Stores
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    // Get a Store by ID
    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }

    // Get Stores by City
    public List<Store> getStoresByCity(String city) {
        return storeRepository.findByCity(city);
    }

    // Get Stores by Country
    public List<Store> getStoresByCountry(String country) {
        return storeRepository.findByCountry(country);
    }

    // Update a Store
    public Store updateStore(Long id, Store updatedStore) {
        return storeRepository.findById(id)
                .map(existingStore -> {
                    existingStore.setName(updatedStore.getName());
                    existingStore.setAddress(updatedStore.getAddress());
                    existingStore.setCity(updatedStore.getCity());
                    existingStore.setState(updatedStore.getState());
                    existingStore.setZipCode(updatedStore.getZipCode());
                    existingStore.setCountry(updatedStore.getCountry());
                    existingStore.setUpdatedAt(LocalDateTime.now());
                    return storeRepository.save(existingStore);
                })
                .orElseThrow(() -> new RuntimeException("Store not found with ID: " + id));
    }

    // Delete a Store
    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
    }
}
