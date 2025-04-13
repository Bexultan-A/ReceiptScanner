package com.example.recieptscaner.controller;

import com.example.recieptscaner.model.User;
import com.example.recieptscaner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        System.out.println("USER AFTER REGISTER:" + user.getUsername() + "Password:" + user.getPasswordHash());
        userService.createUser(user);
        return "User has created";
    }
}
