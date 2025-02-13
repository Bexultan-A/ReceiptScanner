package com.example.recieptscaner.repository;

import com.example.recieptscaner.model.Receipt;
import com.example.recieptscaner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
