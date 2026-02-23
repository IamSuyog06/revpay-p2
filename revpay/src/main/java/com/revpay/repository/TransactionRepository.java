package com.revpay.repository;

import com.revpay.entity.Transaction;
import com.revpay.entity.User;
import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // get all transactions where user is sender or receiver
    @Query("SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.createdAt DESC")
    List<Transaction> findAllByUser(@Param("user") User user);

    List<Transaction> findBySenderOrderByCreatedAtDesc(User sender);

    List<Transaction> findByReceiverOrderByCreatedAtDesc(User receiver);

    // filter transactions with optional parameters
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sender = :user OR t.receiver = :user) AND " +
            "(:type IS NULL OR t.type = :type) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR t.createdAt <= :endDate) AND " +
            "(:minAmount IS NULL OR t.amount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR t.amount <= :maxAmount) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findWithFilters(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );

    // search by sender or receiver name
    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sender = :user OR t.receiver = :user) AND " +
            "(LOWER(t.sender.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.receiver.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> searchByName(
            @Param("user") User user,
            @Param("keyword") String keyword
    );
}