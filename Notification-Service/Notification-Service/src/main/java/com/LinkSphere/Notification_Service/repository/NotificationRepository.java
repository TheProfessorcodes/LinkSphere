package com.LinkSphere.Notification_Service.repository;

import com.LinkSphere.Notification_Service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
