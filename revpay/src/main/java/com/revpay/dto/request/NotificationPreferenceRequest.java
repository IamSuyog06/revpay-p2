package com.revpay.dto.request;

import lombok.Data;


public class NotificationPreferenceRequest {

    private boolean transactionAlerts;
    private boolean moneyRequestAlerts;
    private boolean cardChangeAlerts;
    private boolean lowBalanceAlerts;
    private boolean invoiceAlerts;
    private boolean loanAlerts;

    public NotificationPreferenceRequest(boolean cardChangeAlerts, boolean invoiceAlerts, boolean loanAlerts, boolean lowBalanceAlerts, boolean moneyRequestAlerts, boolean transactionAlerts) {
        this.cardChangeAlerts = cardChangeAlerts;
        this.invoiceAlerts = invoiceAlerts;
        this.loanAlerts = loanAlerts;
        this.lowBalanceAlerts = lowBalanceAlerts;
        this.moneyRequestAlerts = moneyRequestAlerts;
        this.transactionAlerts = transactionAlerts;
    }

    public NotificationPreferenceRequest() {
    }

    public boolean isCardChangeAlerts() {
        return cardChangeAlerts;
    }

    public void setCardChangeAlerts(boolean cardChangeAlerts) {
        this.cardChangeAlerts = cardChangeAlerts;
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

    @Override
    public String toString() {
        return "NotificationPreferenceRequest{" +
                "cardChangeAlerts=" + cardChangeAlerts +
                ", transactionAlerts=" + transactionAlerts +
                ", moneyRequestAlerts=" + moneyRequestAlerts +
                ", lowBalanceAlerts=" + lowBalanceAlerts +
                ", invoiceAlerts=" + invoiceAlerts +
                ", loanAlerts=" + loanAlerts +
                '}';
    }
}