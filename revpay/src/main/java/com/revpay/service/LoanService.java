package com.revpay.service;

import com.revpay.dto.request.LoanApplicationRequest;
import com.revpay.dto.request.LoanRepaymentRequest;
import com.revpay.dto.response.LoanApplicationResponse;
import com.revpay.dto.response.LoanRepaymentResponse;
import com.revpay.entity.LoanApplication;
import com.revpay.entity.LoanRepayment;
import com.revpay.entity.User;
import com.revpay.entity.Wallet;
import com.revpay.enums.LoanStatus;
import com.revpay.enums.NotificationType;
import com.revpay.enums.Role;
import com.revpay.exception.InsufficientBalanceException;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.LoanApplicationRepository;
import com.revpay.repository.LoanRepaymentRepository;
import com.revpay.repository.WalletRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    private static final Logger logger = LogManager.getLogger(LoanService.class);

    // fixed interest rate for all loans - 10% per annum
    private static final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("10.0");

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private LoanRepaymentRepository loanRepaymentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // apply for a loan
    @Transactional
    public LoanApplicationResponse applyForLoan(LoanApplicationRequest request) {
        User user = getBusinessUser();
        logger.info("Loan application submitted by: {}", user.getEmail());

        // calculate monthly interest rate and EMI
        BigDecimal monthlyRate = ANNUAL_INTEREST_RATE
                .divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);

        BigDecimal emi = calculateEmi(request.getLoanAmount(), monthlyRate, request.getTenure());

        LoanApplication loan = new LoanApplication();
        loan.setUser(user);
        loan.setLoanAmount(request.getLoanAmount());
        loan.setPurpose(request.getPurpose());
        loan.setTenure(request.getTenure());
        loan.setInterestRate(ANNUAL_INTEREST_RATE);
        loan.setEmiAmount(emi);
        loan.setFinancialInfo(request.getFinancialInfo());
        loan.setStatus(LoanStatus.PENDING);
        loanApplicationRepository.save(loan);

        // notify user
        notificationService.sendNotification(user,
                "Loan application of ₹" + request.getLoanAmount() + " submitted successfully. Status: PENDING",
                NotificationType.LOAN);

        logger.info("Loan application created successfully for: {}", user.getEmail());
        return toLoanResponse(loan);
    }

    // get all loan applications for logged in business user
    public List<LoanApplicationResponse> getAllLoans() {
        User user = getBusinessUser();
        logger.info("Fetching all loans for: {}", user.getEmail());
        return loanApplicationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toLoanResponse)
                .collect(Collectors.toList());
    }

    // get loans by status
    public List<LoanApplicationResponse> getLoansByStatus(String status) {
        User user = getBusinessUser();
        LoanStatus loanStatus = LoanStatus.valueOf(status.toUpperCase());
        return loanApplicationRepository.findByUserAndStatusOrderByCreatedAtDesc(user, loanStatus)
                .stream()
                .map(this::toLoanResponse)
                .collect(Collectors.toList());
    }

    // get single loan by ID
    public LoanApplicationResponse getLoanById(Long id) {
        User user = getBusinessUser();
        LoanApplication loan = getLoanByIdAndUser(id, user);
        return toLoanResponse(loan);
    }

    // make a loan repayment - deducts from wallet (simulated)
    @Transactional
    public LoanRepaymentResponse makeRepayment(Long loanId, LoanRepaymentRequest request) {
        User user = getBusinessUser();
        logger.info("Loan repayment for loan {} by: {}", loanId, user.getEmail());

        LoanApplication loan = getLoanByIdAndUser(loanId, user);

        // only approved loans can be repaid
        if (!loan.getStatus().equals(LoanStatus.APPROVED)) {
            throw new UnauthorizedAccessException("Only approved loans can be repaid");
        }

        // verify transaction PIN
        verifyTransactionPin(user, request.getTransactionPin());

        // deduct from wallet
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance for repayment");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        // save repayment record
        LoanRepayment repayment = new LoanRepayment();
        repayment.setLoanApplication(loan);
        repayment.setAmountPaid(request.getAmount());
        loanRepaymentRepository.save(repayment);

        // notify user
        notificationService.sendNotification(user,
                "Loan repayment of ₹" + request.getAmount() + " made for loan #" + loanId,
                NotificationType.LOAN);

        logger.info("Loan repayment successful for loan {} by: {}", loanId, user.getEmail());
        return toRepaymentResponse(repayment);
    }

    // ---- helper methods ----

    private User getBusinessUser() {
        User user = userService.getLoggedInUser();
        if (!user.getRole().equals(Role.BUSINESS)) {
            throw new UnauthorizedAccessException("Only business accounts can access this feature");
        }
        return user;
    }

    private LoanApplication getLoanByIdAndUser(Long id, User user) {
        LoanApplication loan = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found"));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("This loan does not belong to you");
        }
        return loan;
    }

    private void verifyTransactionPin(User user, String pin) {
        if (user.getTransactionPin() == null) {
            throw new UnauthorizedAccessException("Please set a transaction PIN first");
        }
        if (!passwordEncoder.matches(pin, user.getTransactionPin())) {
            throw new UnauthorizedAccessException("Invalid transaction PIN");
        }
    }

    // EMI formula: P * r * (1+r)^n / ((1+r)^n - 1)
    // P = principal, r = monthly rate, n = tenure in months
    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal monthlyRate, int tenure) {
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRPowerN = onePlusR.pow(tenure);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    // calculate total amount paid so far
    private BigDecimal getTotalPaid(LoanApplication loan) {
        return loanRepaymentRepository.findByLoanApplicationOrderByPaidAtDesc(loan)
                .stream()
                .map(LoanRepayment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LoanApplicationResponse toLoanResponse(LoanApplication loan) {
        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setId(loan.getId());
        response.setLoanAmount(loan.getLoanAmount());
        response.setPurpose(loan.getPurpose());
        response.setTenure(loan.getTenure());
        response.setInterestRate(loan.getInterestRate());
        response.setEmiAmount(loan.getEmiAmount());
        response.setFinancialInfo(loan.getFinancialInfo());
        response.setStatus(loan.getStatus().name());
        response.setCreatedAt(loan.getCreatedAt());

        // calculate total paid and remaining
        BigDecimal totalPaid = getTotalPaid(loan);
        BigDecimal totalPayable = loan.getEmiAmount() != null
                ? loan.getEmiAmount().multiply(new BigDecimal(loan.getTenure()))
                : loan.getLoanAmount();

        response.setTotalAmountPaid(totalPaid);
        response.setRemainingAmount(totalPayable.subtract(totalPaid));

        // load repayments
        List<LoanRepaymentResponse> repayments = loanRepaymentRepository
                .findByLoanApplicationOrderByPaidAtDesc(loan)
                .stream()
                .map(this::toRepaymentResponse)
                .collect(Collectors.toList());
        response.setRepayments(repayments);

        return response;
    }

    private LoanRepaymentResponse toRepaymentResponse(LoanRepayment repayment) {
        LoanRepaymentResponse response = new LoanRepaymentResponse();
        response.setId(repayment.getId());
        response.setLoanApplicationId(repayment.getLoanApplication().getId());
        response.setAmountPaid(repayment.getAmountPaid());
        response.setPaidAt(repayment.getPaidAt());
        return response;
    }
}