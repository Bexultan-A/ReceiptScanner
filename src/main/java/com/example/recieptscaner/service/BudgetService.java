package com.example.recieptscaner.service;

import com.example.recieptscaner.model.Budget;
import com.example.recieptscaner.repository.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    // Create Budget
    public Budget createBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    // Get all budgets
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    // Get budget by ID
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    // Get budgets by user ID
    public List<Budget> getBudgetsByUserId(Long userId) {
        return budgetRepository.findByUser_UserId(userId);
    }

    // Update Budget
    public Budget updateBudget(Long id, Budget updatedBudget) {
        return budgetRepository.findById(id)
                .map(existingBudget -> {
                    existingBudget.setCategory(updatedBudget.getCategory());
                    existingBudget.setBudgetAmount(updatedBudget.getBudgetAmount());
                    existingBudget.setPeriodStart(updatedBudget.getPeriodStart());
                    existingBudget.setPeriodEnd(updatedBudget.getPeriodEnd());
                    existingBudget.setUpdatedAt(updatedBudget.getUpdatedAt());
                    return budgetRepository.save(existingBudget);
                })
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + id));
    }

    // Delete Budget
    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }
}
