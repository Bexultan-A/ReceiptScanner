package com.example.recieptscaner.controller;

import com.example.recieptscaner.model.Store;
import com.example.recieptscaner.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // Create a Store
    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody Store store) {
        Store savedStore = storeService.createStore(store);
        return ResponseEntity.ok(savedStore);
    }

    // Get all Stores
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    // Get a Store by ID
    @GetMapping("/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable Long id) {
        Optional<Store> store = storeService.getStoreById(id);
        return store.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get Stores by City
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Store>> getStoresByCity(@PathVariable String city) {
        return ResponseEntity.ok(storeService.getStoresByCity(city));
    }

    // Get Stores by Country
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Store>> getStoresByCountry(@PathVariable String country) {
        return ResponseEntity.ok(storeService.getStoresByCountry(country));
    }

    // Update a Store
    @PutMapping("/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable Long id, @RequestBody Store updatedStore) {
        try {
            Store store = storeService.updateStore(id, updatedStore);
            return ResponseEntity.ok(store);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a Store
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}
