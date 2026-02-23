package com.revpay.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_repayments")

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

    public LoanRepayment(BigDecimal amountPaid, Long id, LoanApplication loanApplication, LocalDateTime paidAt) {
        this.amountPaid = amountPaid;
        this.id = id;
        this.loanApplication = loanApplication;
        this.paidAt = paidAt;
    }

    public LoanRepayment() {
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public String toString() {
        return "LoanRepayment{" +
                "amountPaid=" + amountPaid +
                ", id=" + id +
                ", loanApplication=" + loanApplication +
                ", paidAt=" + paidAt +
                '}';
    }
}
