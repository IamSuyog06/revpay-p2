package com.revpay.dto.request;

import jakarta.validation.constraints.NotBlank;



public class LoginRequest {

    // can be email or phone
    @NotBlank(message = "Email or phone is required")
    private String emailOrPhone;

    @NotBlank(message = "Password is required")
    private String password;

    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        this.emailOrPhone = emailOrPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "emailOrPhone='" + emailOrPhone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}