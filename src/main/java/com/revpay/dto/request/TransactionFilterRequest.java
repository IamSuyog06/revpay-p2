package com.revpay.dto.request;


import java.math.BigDecimal;
import java.time.LocalDateTime;


public class TransactionFilterRequest {

    // all fields are optional - null means no filter applied
    private String type;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    // for search
    private String keyword;

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "TransactionFilterRequest{" +
                "endDate=" + endDate +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", startDate=" + startDate +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}