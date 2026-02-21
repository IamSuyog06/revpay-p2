package com.revpay.service;

import com.revpay.entity.Notification;
import com.revpay.entity.User;
import com.revpay.enums.NotificationType;
import com.revpay.repository.NotificationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    // this method is called from other services to create a notification
    public void sendNotification(User user, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notificationRepository.save(notification);
        logger.info("Notification sent to {}: {}", user.getEmail(), message);
    }
}