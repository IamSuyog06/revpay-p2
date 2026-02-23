package com.revpay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TransactionPinRequest {

    @NotBlank(message = "PIN is required")
    @Size(min = 4, max = 4, message = "PIN must be exactly 4 digits")
    private String pin;

    // only required when changing existing PIN
    private String currentPin;

    public String getCurrentPin() {
        return currentPin;
    }

    public void setCurrentPin(String currentPin) {
        this.currentPin = currentPin;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "TransactionPinRequest{" +
                "currentPin='" + currentPin + '\'' +
                ", pin='" + pin + '\'' +
                '}';
    }
}