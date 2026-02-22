package com.revpay.repository;

import com.revpay.entity.Invoice;
import com.revpay.entity.User;
import com.revpay.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByBusinessUserOrderByCreatedAtDesc(User businessUser);

    List<Invoice> findByBusinessUserAndStatusOrderByCreatedAtDesc(User businessUser, InvoiceStatus status);

    List<Invoice> findByBusinessUserAndStatusNotOrderByCreatedAtDesc(User businessUser, InvoiceStatus status);
}