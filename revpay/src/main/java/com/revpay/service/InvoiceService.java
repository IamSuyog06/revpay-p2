package com.revpay.service;

import com.revpay.dto.request.CreateInvoiceRequest;
import com.revpay.dto.request.InvoiceLineItemRequest;
import com.revpay.dto.response.InvoiceLineItemResponse;
import com.revpay.dto.response.InvoiceResponse;
import com.revpay.entity.Invoice;
import com.revpay.entity.InvoiceLineItem;
import com.revpay.entity.User;
import com.revpay.enums.InvoiceStatus;
import com.revpay.enums.NotificationType;
import com.revpay.enums.Role;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.InvoiceLineItemRepository;
import com.revpay.repository.InvoiceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private static final Logger logger = LogManager.getLogger(InvoiceService.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceLineItemRepository invoiceLineItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    // create a new invoice
    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        User user = getBusinessUser();
        logger.info("Creating invoice for business user: {}", user.getEmail());

        // calculate total from line items
        BigDecimal total = calculateTotal(request.getLineItems());

        Invoice invoice = new Invoice();
        invoice.setBusinessUser(user);
        invoice.setCustomerName(request.getCustomerName());
        invoice.setCustomerEmail(request.getCustomerEmail());
        invoice.setCustomerAddress(request.getCustomerAddress());
        invoice.setTotalAmount(total);
        invoice.setPaymentTerms(request.getPaymentTerms());
        invoice.setDueDate(request.getDueDate());
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoiceRepository.save(invoice);

        // save line items
        List<InvoiceLineItem> lineItems = request.getLineItems()
                .stream()
                .map(item -> toLineItemEntity(item, invoice))
                .collect(Collectors.toList());
        invoiceLineItemRepository.saveAll(lineItems);

        invoice.setLineItems(lineItems);

        logger.info("Invoice created successfully for: {}", user.getEmail());
        return toInvoiceResponse(invoice);
    }

    // get all invoices for logged in business user
    public List<InvoiceResponse> getAllInvoices() {
        User user = getBusinessUser();
        logger.info("Fetching all invoices for: {}", user.getEmail());
        return invoiceRepository.findByBusinessUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toInvoiceResponse)
                .collect(Collectors.toList());
    }

    // get invoices by status
    public List<InvoiceResponse> getInvoicesByStatus(String status) {
        User user = getBusinessUser();
        InvoiceStatus invoiceStatus = InvoiceStatus.valueOf(status.toUpperCase());
        return invoiceRepository.findByBusinessUserAndStatusOrderByCreatedAtDesc(user, invoiceStatus)
                .stream()
                .map(this::toInvoiceResponse)
                .collect(Collectors.toList());
    }

    // get single invoice by ID
    public InvoiceResponse getInvoiceById(Long id) {
        User user = getBusinessUser();
        Invoice invoice = getInvoiceByIdAndUser(id, user);
        return toInvoiceResponse(invoice);
    }

    // send invoice to customer - changes status from DRAFT to SENT
    @Transactional
    public InvoiceResponse sendInvoice(Long id) {
        User user = getBusinessUser();
        logger.info("Sending invoice {} for: {}", id, user.getEmail());

        Invoice invoice = getInvoiceByIdAndUser(id, user);

        if (!invoice.getStatus().equals(InvoiceStatus.DRAFT)) {
            throw new UnauthorizedAccessException("Only draft invoices can be sent");
        }

        invoice.setStatus(InvoiceStatus.SENT);
        invoiceRepository.save(invoice);

        // notify business user
        notificationService.sendNotification(user,
                "Invoice #" + invoice.getId() + " sent to " + invoice.getCustomerEmail(),
                NotificationType.INVOICE);

        logger.info("Invoice {} sent successfully", id);
        return toInvoiceResponse(invoice);
    }

    // mark invoice as paid manually
    @Transactional
    public InvoiceResponse markAsPaid(Long id) {
        User user = getBusinessUser();
        logger.info("Marking invoice {} as paid for: {}", id, user.getEmail());

        Invoice invoice = getInvoiceByIdAndUser(id, user);

        if (invoice.getStatus().equals(InvoiceStatus.CANCELLED)) {
            throw new UnauthorizedAccessException("Cancelled invoices cannot be marked as paid");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        // notify business user
        notificationService.sendNotification(user,
                "Invoice #" + invoice.getId() + " marked as paid. Amount: ₹" + invoice.getTotalAmount(),
                NotificationType.INVOICE);

        logger.info("Invoice {} marked as paid", id);
        return toInvoiceResponse(invoice);
    }

    // cancel an invoice
    @Transactional
    public InvoiceResponse cancelInvoice(Long id) {
        User user = getBusinessUser();
        logger.info("Cancelling invoice {} for: {}", id, user.getEmail());

        Invoice invoice = getInvoiceByIdAndUser(id, user);

        if (invoice.getStatus().equals(InvoiceStatus.PAID)) {
            throw new UnauthorizedAccessException("Paid invoices cannot be cancelled");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);

        logger.info("Invoice {} cancelled", id);
        return toInvoiceResponse(invoice);
    }

    // ---- helper methods ----

    // only business users can access invoice features
    private User getBusinessUser() {
        User user = userService.getLoggedInUser();
        if (!user.getRole().equals(Role.BUSINESS)) {
            throw new UnauthorizedAccessException("Only business accounts can access this feature");
        }
        return user;
    }

    private Invoice getInvoiceByIdAndUser(Long id, User user) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        if (!invoice.getBusinessUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("This invoice does not belong to you");
        }
        return invoice;
    }

    // calculate total amount from all line items including tax
    private BigDecimal calculateTotal(List<InvoiceLineItemRequest> items) {
        return items.stream()
                .map(item -> {
                    BigDecimal basePrice = item.getUnitPrice()
                            .multiply(new BigDecimal(item.getQuantity()));
                    BigDecimal taxAmount = basePrice.multiply(item.getTax())
                            .divide(new BigDecimal("100"));
                    return basePrice.add(taxAmount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private InvoiceLineItem toLineItemEntity(InvoiceLineItemRequest request, Invoice invoice) {
        InvoiceLineItem item = new InvoiceLineItem();
        item.setInvoice(invoice);
        item.setDescription(request.getDescription());
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setTax(request.getTax());

        // total = (unitPrice * quantity) + tax%
        BigDecimal basePrice = request.getUnitPrice()
                .multiply(new BigDecimal(request.getQuantity()));
        BigDecimal taxAmount = basePrice.multiply(request.getTax())
                .divide(new BigDecimal("100"));
        item.setTotalPrice(basePrice.add(taxAmount));

        return item;
    }

    public InvoiceResponse toInvoiceResponse(Invoice invoice) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(invoice.getId());
        response.setBusinessUserName(invoice.getBusinessUser().getFullName());
        response.setBusinessUserEmail(invoice.getBusinessUser().getEmail());
        response.setCustomerName(invoice.getCustomerName());
        response.setCustomerEmail(invoice.getCustomerEmail());
        response.setCustomerAddress(invoice.getCustomerAddress());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setPaymentTerms(invoice.getPaymentTerms());
        response.setDueDate(invoice.getDueDate());
        response.setStatus(invoice.getStatus().name());
        response.setCreatedAt(invoice.getCreatedAt());

        // load line items
        List<InvoiceLineItemResponse> lineItems = invoiceLineItemRepository
                .findByInvoice(invoice)
                .stream()
                .map(this::toLineItemResponse)
                .collect(Collectors.toList());
        response.setLineItems(lineItems);

        return response;
    }

    private InvoiceLineItemResponse toLineItemResponse(InvoiceLineItem item) {
        InvoiceLineItemResponse response = new InvoiceLineItemResponse();
        response.setId(item.getId());
        response.setDescription(item.getDescription());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setTax(item.getTax());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }
}