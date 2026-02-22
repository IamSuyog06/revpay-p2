package com.revpay.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public class LoanApplicationResponse {

    private Long id;
    private BigDecimal loanAmount;
    private String purpose;
    private Integer tenure;
    private BigDecimal interestRate;
    private BigDecimal emiAmount;
    private String financialInfo;
    private String status;
    private BigDecimal totalAmountPaid;
    private BigDecimal remainingAmount;
    private List<LoanRepaymentResponse> repayments;
    private LocalDateTime createdAt;

    public LoanApplicationResponse(LocalDateTime createdAt, BigDecimal emiAmount, String financialInfo, Long id, BigDecimal interestRate, BigDecimal loanAmount, String purpose, BigDecimal remainingAmount, List<LoanRepaymentResponse> repayments, String status, Integer tenure, BigDecimal totalAmountPaid) {
        this.createdAt = createdAt;
        this.emiAmount = emiAmount;
        this.financialInfo = financialInfo;
        this.id = id;
        this.interestRate = interestRate;
        this.loanAmount = loanAmount;
        this.purpose = purpose;
        this.remainingAmount = remainingAmount;
        this.repayments = repayments;
        this.status = status;
        this.tenure = tenure;
        this.totalAmountPaid = totalAmountPaid;
    }

    public LoanApplicationResponse() {
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFinancialInfo() {
        return financialInfo;
    }

    public void setFinancialInfo(String financialInfo) {
        this.financialInfo = financialInfo;
    }

    public BigDecimal getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(BigDecimal emiAmount) {
        this.emiAmount = emiAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public List<LoanRepaymentResponse> getRepayments() {
        return repayments;
    }

    public void setRepayments(List<LoanRepaymentResponse> repayments) {
        this.repayments = repayments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTenure() {
        return tenure;
    }

    public void setTenure(Integer tenure) {
        this.tenure = tenure;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    @Override
    public String toString() {
        return "LoanApplicationResponse{" +
                "createdAt=" + createdAt +
                ", id=" + id +
                ", loanAmount=" + loanAmount +
                ", purpose='" + purpose + '\'' +
                ", tenure=" + tenure +
                ", interestRate=" + interestRate +
                ", emiAmount=" + emiAmount +
                ", financialInfo='" + financialInfo + '\'' +
                ", status='" + status + '\'' +
                ", totalAmountPaid=" + totalAmountPaid +
                ", remainingAmount=" + remainingAmount +
                ", repayments=" + repayments +
                '}';
    }
}