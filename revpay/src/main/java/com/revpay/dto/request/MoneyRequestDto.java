package com.revpay.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;


public class MoneyRequestDto {

    // person we are requesting money from
    @NotBlank(message = "Recipient identifier is required")
    private String recipientIdentifier;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1")
    private BigDecimal amount;

    private String purpose;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRecipientIdentifier() {
        return recipientIdentifier;
    }

    public void setRecipientIdentifier(String recipientIdentifier) {
        this.recipientIdentifier = recipientIdentifier;
    }

    @Override
    public String toString() {
        return "MoneyRequest{" +
                "amount=" + amount +
                ", recipientIdentifier='" + recipientIdentifier + '\'' +
                ", purpose='" + purpose + '\'' +
                '}';
    }


}