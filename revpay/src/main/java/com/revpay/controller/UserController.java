package com.revpay.controller;

import com.revpay.dto.request.ChangePasswordRequest;
import com.revpay.dto.request.TransactionPinRequest;
import com.revpay.dto.request.UpdateProfileRequest;
import com.revpay.dto.response.UserResponse;
import com.revpay.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() {
        logger.info("Get profile request received");
        return ResponseEntity.ok(userService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        logger.info("Update profile request received");
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        logger.info("Change password request received");
        return ResponseEntity.ok(userService.changePassword(request));
    }

    @PutMapping("/transaction-pin")
    public ResponseEntity<String> setTransactionPin(@Valid @RequestBody TransactionPinRequest request) {
        logger.info("Set transaction PIN request received");
        return ResponseEntity.ok(userService.setTransactionPin(request));
    }
}