package com.LinkSphere.postService.entity;

import jakarta.persistence.*;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "post_likes")
public class PostLike {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long postId;
    @CreationTimestamp
    private LocalDateTime createdAt;

}
