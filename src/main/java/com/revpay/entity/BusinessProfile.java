package com.revpay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String businessType;

    @Column(nullable = false, unique = true)
    private String taxId;

    @Column(nullable = false)
    private String businessAddress;

    private String verificationDocumentPath;

    // PENDING, APPROVED, REJECTED
    @Column(nullable = false)
    private String verificationStatus = "PENDING";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
