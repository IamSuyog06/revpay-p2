package com.revpay.repository;

import com.revpay.entity.MoneyRequest;
import com.revpay.entity.User;
import com.revpay.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoneyRequestRepository extends JpaRepository<MoneyRequest, Long> {

    // requests coming in to this user
    List<MoneyRequest> findByRecipientOrderByCreatedAtDesc(User recipient);

    // requests this user sent out
    List<MoneyRequest> findByRequesterOrderByCreatedAtDesc(User requester);

    // pending requests coming in
    List<MoneyRequest> findByRecipientAndStatus(User recipient, RequestStatus status);
}