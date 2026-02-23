package com.revpay.entity;


import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_line_items")

public class InvoiceLineItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal tax;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    public InvoiceLineItem(String description, Long id, Invoice invoice, Integer quantity, BigDecimal tax, BigDecimal totalPrice, BigDecimal unitPrice) {
        this.description = description;
        this.id = id;
        this.invoice = invoice;
        this.quantity = quantity;
        this.tax = tax;
        this.totalPrice = totalPrice;
        this.unitPrice = unitPrice;
    }

    public InvoiceLineItem() {
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

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
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
        return "InvoiceLineItem{" +
                "description='" + description + '\'' +
                ", id=" + id +
                ", invoice=" + invoice +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", tax=" + tax +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
