package com.revpay.service;

import com.revpay.dto.request.AddPaymentMethodRequest;
import com.revpay.dto.response.PaymentMethodResponse;
import com.revpay.entity.PaymentMethod;
import com.revpay.entity.User;
import com.revpay.enums.NotificationType;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.PaymentMethodRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {

    private static final Logger logger = LogManager.getLogger(PaymentMethodService.class);

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public PaymentMethodResponse addPaymentMethod(AddPaymentMethodRequest request) {
        User user = userService.getLoggedInUser();
        logger.info("Adding payment method for: {}", user.getEmail());

        // we never store full card number or CVV
        // only last 4 digits of card are stored
        String lastFour = request.getCardNumber()
                .substring(request.getCardNumber().length() - 4);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setUser(user);
        paymentMethod.setCardHolderName(request.getCardHolderName());
        paymentMethod.setLastFourDigits(lastFour);
        paymentMethod.setExpiryMonth(request.getExpiryMonth());
        paymentMethod.setExpiryYear(request.getExpiryYear());
        paymentMethod.setCardType(request.getCardType());
        paymentMethod.setBillingAddress(request.getBillingAddress());

        // if this is the first card, make it default automatically
        List<PaymentMethod> existing = paymentMethodRepository.findByUser(user);
        paymentMethod.setDefault(existing.isEmpty());

        paymentMethodRepository.save(paymentMethod);

        // notify user
        notificationService.sendNotification(user,
                "New " + request.getCardType() + " card ending " + lastFour + " added to your account",
                NotificationType.CARD_CHANGE);

        logger.info("Payment method added for: {}", user.getEmail());
        return toPaymentMethodResponse(paymentMethod);
    }

    public List<PaymentMethodResponse> getAllPaymentMethods() {
        User user = userService.getLoggedInUser();
        return paymentMethodRepository.findByUser(user)
                .stream()
                .map(this::toPaymentMethodResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentMethodResponse setDefaultPaymentMethod(Long paymentMethodId) {
        User user = userService.getLoggedInUser();
        logger.info("Setting default payment method {} for: {}", paymentMethodId, user.getEmail());

        // remove default from current default card
        paymentMethodRepository.findByUserAndIsDefaultTrue(user)
                .ifPresent(current -> {
                    current.setDefault(false);
                    paymentMethodRepository.save(current);
                });

        // set new default
        PaymentMethod paymentMethod = getPaymentMethodByIdAndUser(paymentMethodId, user);
        paymentMethod.setDefault(true);
        paymentMethodRepository.save(paymentMethod);

        logger.info("Default payment method updated for: {}", user.getEmail());
        return toPaymentMethodResponse(paymentMethod);
    }

    @Transactional
    public String deletePaymentMethod(Long paymentMethodId) {
        User user = userService.getLoggedInUser();
        logger.info("Deleting payment method {} for: {}", paymentMethodId, user.getEmail());

        PaymentMethod paymentMethod = getPaymentMethodByIdAndUser(paymentMethodId, user);

        paymentMethodRepository.delete(paymentMethod);

        // notify user
        notificationService.sendNotification(user,
                "Card ending " + paymentMethod.getLastFourDigits() + " removed from your account",
                NotificationType.CARD_CHANGE);

        logger.info("Payment method deleted for: {}", user.getEmail());
        return "Payment method deleted successfully";
    }

    // ---- helper methods ----

    private PaymentMethod getPaymentMethodByIdAndUser(Long id, User user) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        if (!paymentMethod.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("This payment method does not belong to you");
        }
        return paymentMethod;
    }

    private PaymentMethodResponse toPaymentMethodResponse(PaymentMethod paymentMethod) {
        PaymentMethodResponse response = new PaymentMethodResponse();
        response.setId(paymentMethod.getId());
        response.setCardHolderName(paymentMethod.getCardHolderName());
        response.setLastFourDigits(paymentMethod.getLastFourDigits());
        response.setExpiryMonth(paymentMethod.getExpiryMonth());
        response.setExpiryYear(paymentMethod.getExpiryYear());
        response.setCardType(paymentMethod.getCardType());
        response.setBillingAddress(paymentMethod.getBillingAddress());
        response.setDefault(paymentMethod.isDefault());
        response.setCreatedAt(paymentMethod.getCreatedAt());
        return response;
    }
}