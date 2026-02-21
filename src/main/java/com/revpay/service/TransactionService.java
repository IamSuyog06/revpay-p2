package com.revpay.service;

import com.revpay.dto.request.TransactionFilterRequest;
import com.revpay.dto.response.TransactionResponse;
import com.revpay.entity.Transaction;
import com.revpay.entity.User;
import com.revpay.enums.TransactionStatus;
import com.revpay.enums.TransactionType;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.repository.TransactionRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger logger = LogManager.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    // get all transactions for logged in user
    public List<TransactionResponse> getAllTransactions() {
        User user = userService.getLoggedInUser();
        logger.info("Fetching all transactions for: {}", user.getEmail());
        return transactionRepository.findAllByUser(user)
                .stream()
                .map(walletService::toTransactionResponse)
                .collect(Collectors.toList());
    }

    // get single transaction by ID
    public TransactionResponse getTransactionById(Long id) {
        User user = userService.getLoggedInUser();
        logger.info("Fetching transaction {} for: {}", id, user.getEmail());

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // make sure this transaction belongs to the logged in user
        boolean isSender = transaction.getSender() != null &&
                transaction.getSender().getId().equals(user.getId());
        boolean isReceiver = transaction.getReceiver() != null &&
                transaction.getReceiver().getId().equals(user.getId());

        if (!isSender && !isReceiver) {
            throw new ResourceNotFoundException("Transaction not found");
        }

        return walletService.toTransactionResponse(transaction);
    }

    // filter transactions
    public List<TransactionResponse> filterTransactions(TransactionFilterRequest request) {
        User user = userService.getLoggedInUser();
        logger.info("Filtering transactions for: {}", user.getEmail());

        // convert string to enum - null if not provided
        TransactionType type = null;
        if (request.getType() != null && !request.getType().isEmpty()) {
            type = TransactionType.valueOf(request.getType().toUpperCase());
        }

        TransactionStatus status = null;
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            status = TransactionStatus.valueOf(request.getStatus().toUpperCase());
        }

        return transactionRepository.findWithFilters(
                        user,
                        type,
                        status,
                        request.getStartDate(),
                        request.getEndDate(),
                        request.getMinAmount(),
                        request.getMaxAmount()
                )
                .stream()
                .map(walletService::toTransactionResponse)
                .collect(Collectors.toList());
    }

    // search transactions by name or transaction ID
    public List<TransactionResponse> searchTransactions(String keyword) {
        User user = userService.getLoggedInUser();
        logger.info("Searching transactions for: {} with keyword: {}", user.getEmail(), keyword);

        // if keyword looks like a number, search by transaction ID first
        try {
            Long transactionId = Long.parseLong(keyword);
            Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
            if (transaction != null) {
                boolean isSender = transaction.getSender() != null &&
                        transaction.getSender().getId().equals(user.getId());
                boolean isReceiver = transaction.getReceiver() != null &&
                        transaction.getReceiver().getId().equals(user.getId());
                if (isSender || isReceiver) {
                    return List.of(walletService.toTransactionResponse(transaction));
                }
            }
        } catch (NumberFormatException e) {
            // keyword is not a number so search by name
        }

        return transactionRepository.searchByName(user, keyword)
                .stream()
                .map(walletService::toTransactionResponse)
                .collect(Collectors.toList());
    }

    // export transactions to CSV
    public void exportToCsv(HttpServletResponse response) throws IOException {
        User user = userService.getLoggedInUser();
        logger.info("Exporting transactions to CSV for: {}", user.getEmail());

        List<Transaction> transactions = transactionRepository.findAllByUser(user);

        // set response headers so browser downloads the file
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");

        PrintWriter writer = response.getWriter();

        // write CSV header row
        writer.println("Transaction ID,Type,Status,Amount,Sender,Receiver,Note,Date");

        // write each transaction as a row
        for (Transaction t : transactions) {
            String senderName = t.getSender() != null ? t.getSender().getFullName() : "N/A";
            String receiverName = t.getReceiver() != null ? t.getReceiver().getFullName() : "N/A";
            String note = t.getNote() != null ? t.getNote() : "";

            writer.println(
                    t.getId() + "," +
                            t.getType() + "," +
                            t.getStatus() + "," +
                            t.getAmount() + "," +
                            senderName + "," +
                            receiverName + "," +
                            note + "," +
                            t.getCreatedAt()
            );
        }

        writer.flush();
        logger.info("CSV export completed for: {}", user.getEmail());
    }
}