package com.LinkSphere.Notification_Service.consumer;

import com.LinkSphere.Notification_Service.entity.Notification;
import com.LinkSphere.Notification_Service.repository.NotificationRepository;
import com.LinkSphere.Notification_Service.service.NotificationService;
import com.LinkSphere.postService.event.PostCreated;
import com.LinkSphere.postService.event.PostLiked;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "post_created_topic")
    public void handlePostCreated(PostCreated postCreated) {
        log.info("Received postCreated topic:{}",postCreated);
        String message=String.format("Your connection with id:%d has created with this post:%s",postCreated.getOwnerUserId(),postCreated.getContent());
        Notification notification=Notification.builder()
                .message(message)
                .userId(postCreated.getUserId())
                .build();
        notificationService.addNotification(notification);
    }

    @KafkaListener(topics="post_liked_topic")
    public void handlePostLiked(PostLiked  postLiked) {
        log.info("Received postLiked topic:{}",postLiked);
        String message=String.format("User with id:%d has liked your post with id:%d",postLiked.getLikedByUserId(),postLiked.getPostId());
        Notification notification=Notification.builder()
                .message(message)
                .userId(postLiked.getOwnerUserId())
                .build();
        notificationService.addNotification(notification);
    }

}
