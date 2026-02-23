package com.revpay.service;

import com.revpay.dto.request.NotificationPreferenceRequest;
import com.revpay.dto.response.NotificationPreferenceResponse;
import com.revpay.dto.response.NotificationResponse;
import com.revpay.entity.Notification;
import com.revpay.entity.NotificationPreference;
import com.revpay.entity.User;
import com.revpay.enums.NotificationType;
import com.revpay.exception.ResourceNotFoundException;
import com.revpay.exception.UnauthorizedAccessException;
import com.revpay.repository.NotificationPreferenceRepository;
import com.revpay.repository.NotificationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    private UserService userService;

    // this method is called from other services to create a notification
    public void sendNotification(User user, String message, NotificationType type) {

        // check if user has this notification type enabled
        notificationPreferenceRepository.findByUser(user).ifPresent(pref -> {
            if (!isNotificationEnabled(pref, type)) {
                logger.info("Notification type {} disabled for user: {}", type, user.getEmail());
                return;
            }
        });


        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notificationRepository.save(notification);
        logger.info("Notification sent to {}: {}", user.getEmail(), message);
    }

    // get all notifications for logged in user
    public List<NotificationResponse> getAllNotifications() {
        User user = userService.getLoggedInUser();
        logger.info("Fetching notifications for: {}", user.getEmail());
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    // get only unread notifications
    public List<NotificationResponse> getUnreadNotifications() {
        User user = userService.getLoggedInUser();
        return notificationRepository.findByUserAndIsReadFalse(user)
                .stream()
                .map(this::toNotificationResponse)
                .collect(Collectors.toList());
    }

    // get count of unread notifications
    public long getUnreadCount() {
        User user = userService.getLoggedInUser();
        return notificationRepository.findByUserAndIsReadFalse(user).size();
    }

    // mark single notification as read
    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        User user = userService.getLoggedInUser();
        logger.info("Marking notification {} as read for: {}", notificationId, user.getEmail());

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // make sure this notification belongs to logged in user
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("This notification does not belong to you");
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        return toNotificationResponse(notification);
    }

    // mark all notifications as read
    @Transactional
    public String markAllAsRead() {
        User user = userService.getLoggedInUser();
        logger.info("Marking all notifications as read for: {}", user.getEmail());

        List<Notification> unread = notificationRepository.findByUserAndIsReadFalse(user);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);

        logger.info("Marked {} notifications as read for: {}", unread.size(), user.getEmail());
        return "All notifications marked as read";
    }

    // get notification preferences
    public NotificationPreferenceResponse getPreferences() {
        User user = userService.getLoggedInUser();
        NotificationPreference pref = getOrCreatePreference(user);
        return toPreferenceResponse(pref);
    }

    // update notification preferences
    @Transactional
    public NotificationPreferenceResponse updatePreferences(NotificationPreferenceRequest request) {
        User user = userService.getLoggedInUser();
        logger.info("Updating notification preferences for: {}", user.getEmail());

        NotificationPreference pref = getOrCreatePreference(user);
        pref.setTransactionAlerts(request.isTransactionAlerts());
        pref.setMoneyRequestAlerts(request.isMoneyRequestAlerts());
        pref.setCardChangeAlerts(request.isCardChangeAlerts());
        pref.setLowBalanceAlerts(request.isLowBalanceAlerts());
        pref.setInvoiceAlerts(request.isInvoiceAlerts());
        pref.setLoanAlerts(request.isLoanAlerts());

        notificationPreferenceRepository.save(pref);
        logger.info("Notification preferences updated for: {}", user.getEmail());
        return toPreferenceResponse(pref);
    }

    // ---- helper methods ----

    // get existing preference or create default one
    private NotificationPreference getOrCreatePreference(User user) {
        return notificationPreferenceRepository.findByUser(user)
                .orElseGet(() -> {
                    NotificationPreference pref = new NotificationPreference();
                    pref.setUser(user);
                    return notificationPreferenceRepository.save(pref);
                });
    }

    // check if a notification type is enabled in preferences
    private boolean isNotificationEnabled(NotificationPreference pref, NotificationType type) {
        switch (type) {
            case TRANSACTION: return pref.isTransactionAlerts();
            case MONEY_REQUEST: return pref.isMoneyRequestAlerts();
            case CARD_CHANGE: return pref.isCardChangeAlerts();
            case LOW_BALANCE: return pref.isLowBalanceAlerts();
            case INVOICE: return pref.isInvoiceAlerts();
            case LOAN: return pref.isLoanAlerts();
            default: return true;
        }
    }

    private NotificationResponse toNotificationResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setMessage(notification.getMessage());
        response.setType(notification.getType().name());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }

    private NotificationPreferenceResponse toPreferenceResponse(NotificationPreference pref) {
        NotificationPreferenceResponse response = new NotificationPreferenceResponse();
        response.setId(pref.getId());
        response.setTransactionAlerts(pref.isTransactionAlerts());
        response.setMoneyRequestAlerts(pref.isMoneyRequestAlerts());
        response.setCardChangeAlerts(pref.isCardChangeAlerts());
        response.setLowBalanceAlerts(pref.isLowBalanceAlerts());
        response.setInvoiceAlerts(pref.isInvoiceAlerts());
        response.setLoanAlerts(pref.isLoanAlerts());
        return response;
    }

    }