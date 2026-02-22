package com.revpay.dto.response;


import java.math.BigDecimal;

public class InvoiceLineItemResponse {

    private Long id;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal tax;
    private BigDecimal totalPrice;

    public InvoiceLineItemResponse(String description, Integer quantity, Long id, BigDecimal tax, BigDecimal totalPrice, BigDecimal unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.id = id;
        this.tax = tax;
        this.totalPrice = totalPrice;
        this.unitPrice = unitPrice;
    }

    public InvoiceLineItemResponse() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "InvoiceLineItemResponse{" +
                "description='" + description + '\'' +
                ", id=" + id +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", tax=" + tax +
                ", totalPrice=" + totalPrice +
                '}';
    }
}