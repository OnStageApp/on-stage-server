package org.onstage.common.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.onstage.reminder.model.Reminder;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    public void sendNotification(Reminder reminder, String userDeviceToken) {
        Notification notification = Notification.builder()
                .setTitle("On Stage")
                .setBody(reminder.text())
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(userDeviceToken)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
