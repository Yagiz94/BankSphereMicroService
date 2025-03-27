// service/NotificationService.java
package com.example.user.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(String recipient, String message) {
        // Use an external SMS/Email service or in-app notification mechanism.
        // This is a stub implementation.
        System.out.println("Notification sent to " + recipient + ": " + message);
    }
}
