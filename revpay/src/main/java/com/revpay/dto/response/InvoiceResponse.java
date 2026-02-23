package com.revpay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class InvoiceResponse {

    private Long id;
    private String businessUserName;
    private String businessUserEmail;
    private String customerName;
    private String customerEmail;
    private String customerAddress;
    private BigDecimal totalAmount;
    private String paymentTerms;
    private LocalDate dueDate;
    private String status;
    private List<InvoiceLineItemResponse> lineItems;
    private LocalDateTime createdAt;

    public InvoiceResponse(String businessUserEmail, String businessUserName, LocalDateTime createdAt, String customerAddress, String customerEmail, String customerName, LocalDate dueDate, Long id, List<InvoiceLineItemResponse> lineItems, String paymentTerms, String status, BigDecimal totalAmount) {
        this.businessUserEmail = businessUserEmail;
        this.businessUserName = businessUserName;
        this.createdAt = createdAt;
        this.customerAddress = customerAddress;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.dueDate = dueDate;
        this.id = id;
        this.lineItems = lineItems;
        this.paymentTerms = paymentTerms;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public InvoiceResponse() {
    }

    public String getBusinessUserEmail() {
        return businessUserEmail;
    }

    public void setBusinessUserEmail(String businessUserEmail) {
        this.businessUserEmail = businessUserEmail;
    }

    public String getBusinessUserName() {
        return businessUserName;
    }

    public void setBusinessUserName(String businessUserName) {
        this.businessUserName = businessUserName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<InvoiceLineItemResponse> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<InvoiceLineItemResponse> lineItems) {
        this.lineItems = lineItems;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "InvoiceResponse{" +
                "businessUserEmail='" + businessUserEmail + '\'' +
                ", id=" + id +
                ", businessUserName='" + businessUserName + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentTerms='" + paymentTerms + '\'' +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                ", lineItems=" + lineItems +
                ", createdAt=" + createdAt +
                '}';
    }
}