package com.LinkSphere.Notification_Service.consumer;

import com.LinkSphere.ConnectionService.event.ConnectionAcceptedEvent;
import com.LinkSphere.ConnectionService.event.ConnectionRequestEvent;
import com.LinkSphere.Notification_Service.entity.Notification;
import com.LinkSphere.Notification_Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "connection_request_topic")
    public void handleConnectionRequest(ConnectionRequestEvent event) {
        log.info("Received connection request event: {}", event);

        String message = String.format(
                "User with id:%d has sent you a connection request",
                event.getSenderId()
        );

        Notification notification = Notification.builder()
                .message(message)
                .userId(event.getReceiverId())
                .build();

        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "connection_accepted_topic")
    public void handleConnectionAccepted(ConnectionAcceptedEvent event) {
        log.info("Received connection accepted event: {}", event);

        String message = String.format(
                "User with id:%d has accepted your connection request",
                event.getReceiverId()
        );

        Notification notification = Notification.builder()
                .message(message)
                .userId(event.getSenderId())
                .build();

        notificationService.addNotification(notification);
    }
}