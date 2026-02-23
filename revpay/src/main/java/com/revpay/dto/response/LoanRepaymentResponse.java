package com.revpay.dto.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;


public class LoanRepaymentResponse {

    private Long id;
    private Long loanApplicationId;
    private BigDecimal amountPaid;
    private LocalDateTime paidAt;

    public LoanRepaymentResponse(BigDecimal amountPaid, Long id, Long loanApplicationId, LocalDateTime paidAt) {
        this.amountPaid = amountPaid;
        this.id = id;
        this.loanApplicationId = loanApplicationId;
        this.paidAt = paidAt;
    }

    public LoanRepaymentResponse() {
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

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Long loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public String toString() {
        return "LoanRepaymentResponse{" +
                "amountPaid=" + amountPaid +
                ", id=" + id +
                ", loanApplicationId=" + loanApplicationId +
                ", paidAt=" + paidAt +
                '}';
    }
}