package com.revpay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_preferences")
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean transactionAlerts = true;

    @Column(nullable = false)
    private boolean moneyRequestAlerts = true;

    @Column(nullable = false)
    private boolean cardChangeAlerts = true;

    @Column(nullable = false)
    private boolean lowBalanceAlerts = true;

    @Column(nullable = false)
    private boolean invoiceAlerts = true;

    @Column(nullable = false)
    private boolean loanAlerts = true;

    public boolean isCardChangeAlerts() {
        return cardChangeAlerts;
    }

    public void setCardChangeAlerts(boolean cardChangeAlerts) {
        this.cardChangeAlerts = cardChangeAlerts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isInvoiceAlerts() {
        return invoiceAlerts;
    }

    public void setInvoiceAlerts(boolean invoiceAlerts) {
        this.invoiceAlerts = invoiceAlerts;
    }

    public boolean isLoanAlerts() {
        return loanAlerts;
    }

    public void setLoanAlerts(boolean loanAlerts) {
        this.loanAlerts = loanAlerts;
    }

    public boolean isLowBalanceAlerts() {
        return lowBalanceAlerts;
    }

    public void setLowBalanceAlerts(boolean lowBalanceAlerts) {
        this.lowBalanceAlerts = lowBalanceAlerts;
    }

    public boolean isMoneyRequestAlerts() {
        return moneyRequestAlerts;
    }

    public void setMoneyRequestAlerts(boolean moneyRequestAlerts) {
        this.moneyRequestAlerts = moneyRequestAlerts;
    }

    public boolean isTransactionAlerts() {
        return transactionAlerts;
    }

    public void setTransactionAlerts(boolean transactionAlerts) {
        this.transactionAlerts = transactionAlerts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "NotificationPreference{" +
                "cardChangeAlerts=" + cardChangeAlerts +
                ", id=" + id +
                ", user=" + user +
                ", transactionAlerts=" + transactionAlerts +
                ", moneyRequestAlerts=" + moneyRequestAlerts +
                ", lowBalanceAlerts=" + lowBalanceAlerts +
                ", invoiceAlerts=" + invoiceAlerts +
                ", loanAlerts=" + loanAlerts +
                '}';
    }
}
