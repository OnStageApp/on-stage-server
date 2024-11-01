package org.onstage.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationService {

    public void sendPushNotification(String title, String body, String pushToken) {
        Notification pushNotification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        if (Strings.isNotEmpty(pushToken))
            sendPush(pushNotification, pushToken);
    }

    private void sendPush(Notification pushNotification, String pushToken) {
        Message message = Message.builder()
                .setNotification(pushNotification)
                .setToken(pushToken)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
}
