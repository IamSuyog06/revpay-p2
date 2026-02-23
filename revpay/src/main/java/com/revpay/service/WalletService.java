package com.revpay.service;

import com.revpay.dto.request.AddMoneyRequest;
import com.revpay.dto.request.MoneyRequestDto;
import com.revpay.dto.request.SendMoneyRequest;
import com.revpay.dto.request.WithdrawRequest;
import com.revpay.dto.response.MoneyRequestResponse;
import com.revpay.dto.response.TransactionResponse;
import com.revpay.dto.response.WalletResponse;
import com.revpay.entity.*;
import com.revpay.enums.NotificationType;
import com.revpay.enums.RequestStatus;
import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import com.revpay.exception.InsufficientBalanceException;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private static final Logger logger = LogManager.getLogger(WalletService.class);

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MoneyRequestRepository moneyRequestRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // get wallet of logged in user
    public WalletResponse getWallet() {
        User user = userService.getLoggedInUser();
        Wallet wallet = getWalletByUser(user);
        return toWalletResponse(wallet);
    }

    // add money from card to wallet (simulated)
    @Transactional
    public TransactionResponse addMoney(AddMoneyRequest request) {
        User user = userService.getLoggedInUser();
        logger.info("Adding money for user: {}", user.getEmail());

        // verify payment method belongs to this user
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("This payment method does not belong to you");
        }

        // add to wallet
        Wallet wallet = getWalletByUser(user);
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        // create transaction record
        Transaction transaction = new Transaction();
        transaction.setReceiver(user);
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.ADDED_FUNDS);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setNote("Added from card ending " + paymentMethod.getLastFourDigits());
        transactionRepository.save(transaction);

        // notify user
        notificationService.sendNotification(user,
                "₹" + request.getAmount() + " added to your wallet",
                NotificationType.TRANSACTION);

        logger.info("Money added successfully for: {}", user.getEmail());
        return toTransactionResponse(transaction);
    }

    // withdraw from wallet to bank (simulated)
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        User user = userService.getLoggedInUser();
        logger.info("Withdrawal request for user: {}", user.getEmail());

        // verify transaction PIN
        verifyTransactionPin(user, request.getTransactionPin());

        Wallet wallet = getWalletByUser(user);

        // check balance
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        // deduct from wallet
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        // create transaction record
        Transaction transaction = new Transaction();
        transaction.setSender(user);
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setNote("Withdrawn to bank account ending " +
                request.getBankAccountNumber().substring(request.getBankAccountNumber().length() - 4));
        transactionRepository.save(transaction);

        // notify user
        notificationService.sendNotification(user,
                "₹" + request.getAmount() + " withdrawn from your wallet",
                NotificationType.TRANSACTION);

        // check if balance is low after withdrawal
        if (wallet.getBalance().compareTo(new BigDecimal("100")) < 0) {
            notificationService.sendNotification(user,
                    "Low balance alert! Your wallet balance is ₹" + wallet.getBalance(),
                    NotificationType.LOW_BALANCE);
        }

        logger.info("Withdrawal successful for: {}", user.getEmail());
        return toTransactionResponse(transaction);
    }

    // send money to another user
    @Transactional
    public TransactionResponse sendMoney(SendMoneyRequest request) {
        User sender = userService.getLoggedInUser();
        logger.info("Send money request from: {}", sender.getEmail());

        // verify transaction PIN
        verifyTransactionPin(sender, request.getTransactionPin());

        // find recipient by email or phone
        User receiver = userRepository.findByEmail(request.getRecipientIdentifier())
                .orElseGet(() -> userRepository.findByPhone(request.getRecipientIdentifier())
                        .orElseThrow(() -> new ResourceNotFoundException("Recipient not found")));

        // cannot send money to yourself
        if (sender.getId().equals(receiver.getId())) {
            throw new UnauthorizedAccessException("Cannot send money to yourself");
        }

        Wallet senderWallet = getWalletByUser(sender);
        Wallet receiverWallet = getWalletByUser(receiver);

        // check sender balance
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        // deduct from sender
        senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(senderWallet);

        // add to receiver
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));
        walletRepository.save(receiverWallet);

        // create transaction record
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.SENT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setNote(request.getNote());
        transactionRepository.save(transaction);

        // notify both users
        notificationService.sendNotification(sender,
                "₹" + request.getAmount() + " sent to " + receiver.getFullName(),
                NotificationType.TRANSACTION);

        notificationService.sendNotification(receiver,
                "₹" + request.getAmount() + " received from " + sender.getFullName(),
                NotificationType.TRANSACTION);

        // low balance check for sender
        if (senderWallet.getBalance().compareTo(new BigDecimal("100")) < 0) {
            notificationService.sendNotification(sender,
                    "Low balance alert! Your wallet balance is ₹" + senderWallet.getBalance(),
                    NotificationType.LOW_BALANCE);
        }

        logger.info("Money sent successfully from {} to {}", sender.getEmail(), receiver.getEmail());
        return toTransactionResponse(transaction);
    }

    // request money from another user
    @Transactional
    public MoneyRequestResponse requestMoney(MoneyRequestDto request) {
        User requester = userService.getLoggedInUser();
        logger.info("Money request from: {}", requester.getEmail());

        // find recipient
        User recipient = userRepository.findByEmail(request.getRecipientIdentifier())
                .orElseGet(() -> userRepository.findByPhone(request.getRecipientIdentifier())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        // cannot request from yourself
        if (requester.getId().equals(recipient.getId())) {
            throw new UnauthorizedAccessException("Cannot request money from yourself");
        }

        MoneyRequest moneyRequest = new MoneyRequest();
        moneyRequest.setRequester(requester);
        moneyRequest.setRecipient(recipient);
        moneyRequest.setAmount(request.getAmount());
        moneyRequest.setPurpose(request.getPurpose());
        moneyRequestRepository.save(moneyRequest);

        // notify recipient
        notificationService.sendNotification(recipient,
                requester.getFullName() + " requested ₹" + request.getAmount() + " from you",
                NotificationType.MONEY_REQUEST);

        logger.info("Money request created successfully");
        return toMoneyRequestResponse(moneyRequest);
    }

    // get incoming money requests
    public List<MoneyRequestResponse> getIncomingRequests() {
        User user = userService.getLoggedInUser();
        return moneyRequestRepository.findByRecipientOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toMoneyRequestResponse)
                .collect(Collectors.toList());
    }

    // get outgoing money requests
    public List<MoneyRequestResponse> getOutgoingRequests() {
        User user = userService.getLoggedInUser();
        return moneyRequestRepository.findByRequesterOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toMoneyRequestResponse)
                .collect(Collectors.toList());
    }

    // accept a money request - this triggers actual money transfer
    @Transactional
    public TransactionResponse acceptMoneyRequest(Long requestId, String transactionPin) {
        User user = userService.getLoggedInUser();
        logger.info("Accepting money request {} by {}", requestId, user.getEmail());

        MoneyRequest moneyRequest = getMoneyRequestById(requestId);

        // only recipient can accept
        if (!moneyRequest.getRecipient().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not the recipient of this request");
        }

        // only pending requests can be accepted
        if (!moneyRequest.getStatus().equals(RequestStatus.PENDING)) {
            throw new UnauthorizedAccessException("This request is no longer pending");
        }

        // verify PIN
        verifyTransactionPin(user, transactionPin);

        // do the transfer
        Wallet payerWallet = getWalletByUser(user);
        Wallet requesterWallet = getWalletByUser(moneyRequest.getRequester());

        if (payerWallet.getBalance().compareTo(moneyRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        payerWallet.setBalance(payerWallet.getBalance().subtract(moneyRequest.getAmount()));
        walletRepository.save(payerWallet);

        requesterWallet.setBalance(requesterWallet.getBalance().add(moneyRequest.getAmount()));
        walletRepository.save(requesterWallet);

        // update request status
        moneyRequest.setStatus(RequestStatus.ACCEPTED);
        moneyRequestRepository.save(moneyRequest);

        // create transaction record
        Transaction transaction = new Transaction();
        transaction.setSender(user);
        transaction.setReceiver(moneyRequest.getRequester());
        transaction.setAmount(moneyRequest.getAmount());
        transaction.setType(TransactionType.SENT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setNote("Money request accepted");
        transactionRepository.save(transaction);

        // notify both
        notificationService.sendNotification(moneyRequest.getRequester(),
                "₹" + moneyRequest.getAmount() + " received from " + user.getFullName(),
                NotificationType.MONEY_REQUEST);

        notificationService.sendNotification(user,
                "₹" + moneyRequest.getAmount() + " sent to " + moneyRequest.getRequester().getFullName(),
                NotificationType.MONEY_REQUEST);

        logger.info("Money request {} accepted successfully", requestId);
        return toTransactionResponse(transaction);
    }

    // decline a money request
    @Transactional
    public String declineMoneyRequest(Long requestId) {
        User user = userService.getLoggedInUser();
        logger.info("Declining money request {} by {}", requestId, user.getEmail());

        MoneyRequest moneyRequest = getMoneyRequestById(requestId);

        if (!moneyRequest.getRecipient().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not the recipient of this request");
        }

        if (!moneyRequest.getStatus().equals(RequestStatus.PENDING)) {
            throw new UnauthorizedAccessException("This request is no longer pending");
        }

        moneyRequest.setStatus(RequestStatus.DECLINED);
        moneyRequestRepository.save(moneyRequest);

        // notify requester
        notificationService.sendNotification(moneyRequest.getRequester(),
                user.getFullName() + " declined your money request of ₹" + moneyRequest.getAmount(),
                NotificationType.MONEY_REQUEST);

        logger.info("Money request {} declined", requestId);
        return "Money request declined";
    }

    // cancel an outgoing money request
    @Transactional
    public String cancelMoneyRequest(Long requestId) {
        User user = userService.getLoggedInUser();
        logger.info("Cancelling money request {} by {}", requestId, user.getEmail());

        MoneyRequest moneyRequest = getMoneyRequestById(requestId);

        if (!moneyRequest.getRequester().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not the requester of this request");
        }

        if (!moneyRequest.getStatus().equals(RequestStatus.PENDING)) {
            throw new UnauthorizedAccessException("Only pending requests can be cancelled");
        }

        moneyRequest.setStatus(RequestStatus.CANCELLED);
        moneyRequestRepository.save(moneyRequest);

        logger.info("Money request {} cancelled", requestId);
        return "Money request cancelled";
    }

    // ---- helper methods ----

    private Wallet getWalletByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + user.getEmail()));
    }

    private MoneyRequest getMoneyRequestById(Long id) {
        return moneyRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Money request not found"));
    }

    private void verifyTransactionPin(User user, String pin) {
        if (user.getTransactionPin() == null) {
            throw new UnauthorizedAccessException("Please set a transaction PIN first");
        }
        if (!passwordEncoder.matches(pin, user.getTransactionPin())) {
            throw new UnauthorizedAccessException("Invalid transaction PIN");
        }
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getUser().getFullName(),
                wallet.getUser().getEmail()
        );
    }

    public TransactionResponse toTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setType(transaction.getType().name());
        response.setStatus(transaction.getStatus().name());
        response.setNote(transaction.getNote());
        response.setCreatedAt(transaction.getCreatedAt());

        if (transaction.getSender() != null) {
            response.setSenderName(transaction.getSender().getFullName());
            response.setSenderEmail(transaction.getSender().getEmail());
        }
        if (transaction.getReceiver() != null) {
            response.setReceiverName(transaction.getReceiver().getFullName());
            response.setReceiverEmail(transaction.getReceiver().getEmail());
        }
        return response;
    }

    private MoneyRequestResponse toMoneyRequestResponse(MoneyRequest request) {
        MoneyRequestResponse response = new MoneyRequestResponse();
        response.setId(request.getId());
        response.setAmount(request.getAmount());
        response.setPurpose(request.getPurpose());
        response.setStatus(request.getStatus().name());
        response.setCreatedAt(request.getCreatedAt());
        response.setRequesterName(request.getRequester().getFullName());
        response.setRequesterEmail(request.getRequester().getEmail());
        response.setRecipientName(request.getRecipient().getFullName());
        response.setRecipientEmail(request.getRecipient().getEmail());
        return response;
    }
}