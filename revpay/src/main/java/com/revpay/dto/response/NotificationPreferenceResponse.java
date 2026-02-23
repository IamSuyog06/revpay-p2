package com.revpay.dto.response;


public class NotificationPreferenceResponse {

    private Long id;
    private boolean transactionAlerts;
    private boolean moneyRequestAlerts;
    private boolean cardChangeAlerts;
    private boolean lowBalanceAlerts;
    private boolean invoiceAlerts;
    private boolean loanAlerts;

    public NotificationPreferenceResponse(boolean cardChangeAlerts, Long id, boolean invoiceAlerts, boolean loanAlerts, boolean lowBalanceAlerts, boolean moneyRequestAlerts, boolean transactionAlerts) {
        this.cardChangeAlerts = cardChangeAlerts;
        this.id = id;
        this.invoiceAlerts = invoiceAlerts;
        this.loanAlerts = loanAlerts;
        this.lowBalanceAlerts = lowBalanceAlerts;
        this.moneyRequestAlerts = moneyRequestAlerts;
        this.transactionAlerts = transactionAlerts;
    }

    public NotificationPreferenceResponse() {
    }

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

    @Override
    public String toString() {
        return "NotificationPreferenceResponse{" +
                "cardChangeAlerts=" + cardChangeAlerts +
                ", id=" + id +
                ", transactionAlerts=" + transactionAlerts +
                ", moneyRequestAlerts=" + moneyRequestAlerts +
                ", lowBalanceAlerts=" + lowBalanceAlerts +
                ", invoiceAlerts=" + invoiceAlerts +
                ", loanAlerts=" + loanAlerts +
                '}';
    }
}