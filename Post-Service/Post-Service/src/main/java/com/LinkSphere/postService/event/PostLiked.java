package com.LinkSphere.postService.event;

import lombok.*;

@Data
@Builder
public class PostLiked {
    private Long postId;
    private Long ownerUserId;
    private Long likedByUserId;
}
