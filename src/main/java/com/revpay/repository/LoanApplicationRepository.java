package com.revpay.repository;

import com.revpay.entity.LoanApplication;
import com.revpay.entity.User;
import com.revpay.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByUserOrderByCreatedAtDesc(User user);

    List<LoanApplication> findByUserAndStatusOrderByCreatedAtDesc(User user, LoanStatus status);
}