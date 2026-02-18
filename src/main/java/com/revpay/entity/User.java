package com.revpay.entity;

import com.revpay.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
    @Table(name="users")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class User{

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String fullName;

        @Column(nullable = false,unique = true)
        private String email;

        @Column(nullable = false,unique = true)
        private String phone;

        @Column(nullable = false)
        private String securityQuestion;

        @Column(nullable = false)
        private String securityAnswer;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        @Column(nullable = false)
        private boolean isVerified = false;

        @Column(nullable = false)
        private boolean isActive = true;

        private String transactionPin;

        @Column(nullable = false,updatable = false)
        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        @PrePersist
        protected void onCreate(){
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate(){
            updatedAt = LocalDateTime.now();
        }


    }

