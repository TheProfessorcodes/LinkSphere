package com.LinkSphere.Notification_Service.service;

import com.LinkSphere.Notification_Service.entity.Notification;
import com.LinkSphere.Notification_Service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void addNotification(Notification notification) {
        log.info("Adding notification to db:{}",notification.getMessage());
        notification=notificationRepository.save(notification);
        //Send Mailer
    }
}
