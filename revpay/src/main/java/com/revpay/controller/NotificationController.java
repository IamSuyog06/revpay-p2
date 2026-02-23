package com.revpay.controller;

import com.revpay.dto.request.NotificationPreferenceRequest;
import com.revpay.dto.response.NotificationPreferenceResponse;
import com.revpay.dto.response.NotificationResponse;
import com.revpay.service.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LogManager.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        logger.info("Get all notifications request received");
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        logger.info("Get unread notifications request received");
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getUnreadCount());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        logger.info("Mark notification {} as read request received", id);
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead() {
        logger.info("Mark all notifications as read request received");
        return ResponseEntity.ok(notificationService.markAllAsRead());
    }

    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> getPreferences() {
        logger.info("Get notification preferences request received");
        return ResponseEntity.ok(notificationService.getPreferences());
    }

    @PutMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
            @RequestBody NotificationPreferenceRequest request) {
        logger.info("Update notification preferences request received");
        return ResponseEntity.ok(notificationService.updatePreferences(request));
    }
}