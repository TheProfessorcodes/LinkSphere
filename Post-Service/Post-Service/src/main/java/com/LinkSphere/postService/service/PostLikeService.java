package com.LinkSphere.postService.service;

import com.LinkSphere.postService.auth.AuthContextHolder;
import com.LinkSphere.postService.entity.Post;
import com.LinkSphere.postService.entity.PostLike;
import com.LinkSphere.postService.event.PostLiked;
import com.LinkSphere.postService.exception.BadRequestException;
import com.LinkSphere.postService.exception.ResourceNotFoundException;
import com.LinkSphere.postService.respository.PostLikeRepository;
import com.LinkSphere.postService.respository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final ModelMapper modelMapper;
    private final PostRepository postRepository;
    private final KafkaTemplate<Long, PostLiked> postLikedKafkaTemplate;

    @Transactional
    public void likePost(Long postId){

        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("Post like request received from user with userId : {} for post with id : {}", userId, postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFoundException("Post not found with id : " + postId));
        boolean hasAlreadyLiked=postLikeRepository.existsByUserIdAndPostId(userId,postId);
        if(hasAlreadyLiked){
            throw new BadRequestException("Post like request has already been liked.");

        }
        PostLike postLike=new PostLike();
        postLike.setUserId(userId);
        postLike.setPostId(postId);
        postLikeRepository.save(postLike);
        //TODO: send notifications to the owner of the post
        PostLiked postLiked= PostLiked.builder()
                .postId(postId)
                .likedByUserId(userId)
                .ownerUserId(post.getUserId())
                .build();
        postLikedKafkaTemplate.send("post_liked_topic",postLiked);
    }

    @Transactional
    public void unlikePost(Long postId) {
        Long userId=AuthContextHolder.getCurrentUserId();
        log.info("unlike request received from user with userId : {}", userId);
        postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFoundException("Post not found with id : " + postId));
        boolean hasAlreadyLiked=postLikeRepository.existsByUserIdAndPostId(userId,postId);
        if(!hasAlreadyLiked){
            throw new BadRequestException("Post not liked already.");
        }
        postLikeRepository.deleteByUserIdAndPostId(userId,postId);
    }
}
