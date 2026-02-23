package com.revpay.dto.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;




public class WalletResponse {

    private Long id;
    private BigDecimal balance;
    private String ownerName;
    private String ownerEmail;



    public WalletResponse(BigDecimal balance, Long id, String ownerName, String ownerEmail ) {

    }

    public WalletResponse(
            Long id,BigDecimal balance, String ownerName, String ownerEmail) {
        this.balance = balance;
        this.id = id;
        this.ownerName = ownerName;
        this.ownerEmail = ownerEmail;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    @Override
    public String toString() {
        return "WalletResponse{" +
                "balance=" + balance +
                ", id=" + id +
                ", ownerName='" + ownerName + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                '}';
    }
}