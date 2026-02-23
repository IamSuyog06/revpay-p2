package com.revpay.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;


public class CreateInvoiceRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    private String customerAddress;

    private String paymentTerms;

    private LocalDate dueDate;

    @NotEmpty(message = "At least one line item is required")
    private List<InvoiceLineItemRequest> lineItems;

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<InvoiceLineItemRequest> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<InvoiceLineItemRequest> lineItems) {
        this.lineItems = lineItems;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    @Override
    public String toString() {
        return "CreateInvoiceRequest{" +
                "customerAddress='" + customerAddress + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", paymentTerms='" + paymentTerms + '\'' +
                ", dueDate=" + dueDate +
                ", lineItems=" + lineItems +
                '}';
    }
}