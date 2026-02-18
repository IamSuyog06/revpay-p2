package com.revpay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_repayments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRepayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @Column(nullable = false)
    private BigDecimal amountPaid;

    @Column(nullable = false, updatable = false)
    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        paidAt = LocalDateTime.now();
    }
}
