package com.revpay.dto.response;


import java.time.LocalDateTime;


public class PaymentMethodResponse {

    private Long id;
    private String cardHolderName;
    private String lastFourDigits;
    private String expiryMonth;
    private String expiryYear;
    private String cardType;
    private String billingAddress;
    private boolean isDefault;
    private LocalDateTime createdAt;

    public PaymentMethodResponse(String billingAddress, String cardHolderName, String cardType, LocalDateTime createdAt, String expiryMonth, String expiryYear, Long id, boolean isDefault, String lastFourDigits) {
        this.billingAddress = billingAddress;
        this.cardHolderName = cardHolderName;
        this.cardType = cardType;
        this.createdAt = createdAt;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.id = id;
        this.isDefault = isDefault;
        this.lastFourDigits = lastFourDigits;
    }

    public PaymentMethodResponse() {
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    @Override
    public String toString() {
        return "PaymentMethodResponse{" +
                "billingAddress='" + billingAddress + '\'' +
                ", id=" + id +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", lastFourDigits='" + lastFourDigits + '\'' +
                ", expiryMonth='" + expiryMonth + '\'' +
                ", expiryYear='" + expiryYear + '\'' +
                ", cardType='" + cardType + '\'' +
                ", isDefault=" + isDefault +
                ", createdAt=" + createdAt +
                '}';
    }
}