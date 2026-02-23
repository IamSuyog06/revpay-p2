package com.revpay.repository;

import com.revpay.entity.LoanRepayment;
import com.revpay.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {

    List<LoanRepayment> findByLoanApplicationOrderByPaidAtDesc(LoanApplication loanApplication);
}