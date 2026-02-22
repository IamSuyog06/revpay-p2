package com.revpay.entity;


import com.revpay.enums.LoanStatus;
import jakarta.persistence.*;
import jakarta.persistence.Table;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal loanAmount;

    @Column(nullable = false)
    private String purpose;

    // in months
    @Column(nullable = false)
    private Integer tenure;

    private BigDecimal interestRate;

    private BigDecimal emiAmount;

    @Column(nullable = false)
    private String financialInfo;

    private String documentPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        status = LoanStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public LoanApplication(LocalDateTime createdAt, String documentPath, BigDecimal emiAmount, String financialInfo, Long id, BigDecimal interestRate, BigDecimal loanAmount, String purpose, LoanStatus status, Integer tenure, LocalDateTime updatedAt, User user) {
        this.createdAt = createdAt;
        this.documentPath = documentPath;
        this.emiAmount = emiAmount;
        this.financialInfo = financialInfo;
        this.id = id;
        this.interestRate = interestRate;
        this.loanAmount = loanAmount;
        this.purpose = purpose;
        this.status = status;
        this.tenure = tenure;
        this.updatedAt = updatedAt;
        this.user = user;
    }

    public LoanApplication() {
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public BigDecimal getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(BigDecimal emiAmount) {
        this.emiAmount = emiAmount;
    }

    public String getFinancialInfo() {
        return financialInfo;
    }

    public void setFinancialInfo(String financialInfo) {
        this.financialInfo = financialInfo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", user=" + user +
                ", loanAmount=" + loanAmount +
                ", purpose='" + purpose + '\'' +
                ", tenure=" + tenure +
                ", interestRate=" + interestRate +
                ", emiAmount=" + emiAmount +
                ", financialInfo='" + financialInfo + '\'' +
                ", documentPath='" + documentPath + '\'' +
                ", status=" + status +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
