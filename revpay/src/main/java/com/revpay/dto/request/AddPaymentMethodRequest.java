package com.revpay.dto.request;

import jakarta.validation.constraints.NotBlank;


public class AddPaymentMethodRequest {

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "Expiry month is required")
    private String expiryMonth;

    @NotBlank(message = "Expiry year is required")
    private String expiryYear;

    @NotBlank(message = "CVV is required")
    private String cvv;

    @NotBlank(message = "Card type is required")
    private String cardType;

    private String billingAddress;

    public AddPaymentMethodRequest(String billingAddress, String cardHolderName, String cardNumber, String cardType, String cvv, String expiryMonth, String expiryYear) {
        this.billingAddress = billingAddress;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.cvv = cvv;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

    public AddPaymentMethodRequest() {
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    @Override
    public String toString() {
        return "AddPaymentMethodRequest{" +
                "billingAddress='" + billingAddress + '\'' +
                ", cardHolderName='" + cardHolderName + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", expiryMonth='" + expiryMonth + '\'' +
                ", expiryYear='" + expiryYear + '\'' +
                ", cvv='" + cvv + '\'' +
                ", cardType='" + cardType + '\'' +
                '}';
    }
}