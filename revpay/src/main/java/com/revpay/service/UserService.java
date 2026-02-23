package com.revpay.service;

import com.revpay.dto.request.ChangePasswordRequest;
import com.revpay.dto.request.TransactionPinRequest;
import com.revpay.dto.request.UpdateProfileRequest;
import com.revpay.dto.response.UserResponse;
import com.revpay.entity.User;
import com.revpay.exception.DuplicateResourceException;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // this method will be used across all services to get logged in user
    public User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Logged in user not found"));
    }

    // convert user entity to response DTO
    // we never send the password or PIN back to the client
    public UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        response.setVerified(user.isVerified());
        response.setActive(user.isActive());
        response.setHasTransactionPin(user.getTransactionPin() != null);
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public UserResponse getProfile() {
        User user = getLoggedInUser();
        logger.info("Fetching profile for: {}", user.getEmail());
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        User user = getLoggedInUser();
        logger.info("Updating profile for: {}", user.getEmail());

        // check if new phone is already taken by someone else
        if (!user.getPhone().equals(request.getPhone()) &&
                userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already in use");
        }

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        userRepository.save(user);

        logger.info("Profile updated for: {}", user.getEmail());
        return toUserResponse(user);
    }

    @Transactional
    public String changePassword(ChangePasswordRequest request) {
        User user = getLoggedInUser();
        logger.info("Password change request for: {}", user.getEmail());

        // verify current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedAccessException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        logger.info("Password changed successfully for: {}", user.getEmail());
        return "Password changed successfully";
    }

    @Transactional
    public String setTransactionPin(TransactionPinRequest request) {
        User user = getLoggedInUser();
        logger.info("Setting transaction PIN for: {}", user.getEmail());

        // if user already has a PIN, verify current PIN first
        boolean hasExistingPin = user.getTransactionPin() != null;
        if (hasExistingPin) {
            if (!passwordEncoder.matches(request.getCurrentPin(), user.getTransactionPin())) {
                throw new UnauthorizedAccessException("Current PIN is incorrect");
            }
        }

        user.setTransactionPin(passwordEncoder.encode(request.getPin()));
        userRepository.save(user);

        logger.info("Transaction PIN set successfully for: {}", user.getEmail());
        if (hasExistingPin) {
            return "Transaction PIN changed successfully";
        } else {
            return "Transaction PIN set successfully";
        }
    }
}