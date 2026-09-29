package com.LinkSphere.postService.service;

import com.LinkSphere.postService.auth.AuthContextHolder;
import com.LinkSphere.postService.client.ConnectionsServiceClient;
import com.LinkSphere.postService.client.UploaderServiceClient;
import com.LinkSphere.postService.dto.PersonDto;
import com.LinkSphere.postService.dto.PostCreateRequestDto;
import com.LinkSphere.postService.dto.PostDto;
import com.LinkSphere.postService.entity.Post;
import com.LinkSphere.postService.event.PostCreated;
import com.LinkSphere.postService.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.LinkSphere.postService.respository.PostRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsServiceClient connectionsServiceClient;
    private final KafkaTemplate<Long, PostCreated> postCreatedKafkaTemplate;
    private final UploaderServiceClient uploaderServiceClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, MultipartFile file) {
        Long userId=AuthContextHolder.getCurrentUserId();
        log.info("Creating post");

        ResponseEntity<String> imageUrl=uploaderServiceClient.uploadFile(file);

        Post post=modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);
        post.setImageUrl(imageUrl.getBody());

        post=postRepository.save(post);
        List<PersonDto> personDtoList=connectionsServiceClient.getFirstDegreeConnectionsOfUser(userId);
        for(PersonDto person:personDtoList){//sending notification to each connection
            PostCreated postCreated=PostCreated.builder()
                    .postId(post.getId())
                    .content(post.getContent())
                    .userId(person.getUserId())
                    .ownerUserId(userId)
                    .build();
            postCreatedKafkaTemplate.send("post_created_topic",postCreated);
        }
        return modelMapper.map(post, PostDto.class);
    }

    public PostDto getPostById(Long postId) {
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new ResourceNotFoundException("Post not found"+ "with id:"+postId));
        return modelMapper.map(post, PostDto.class);

    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        log.info("Getting all the posts of user with id : {}",userId);
        List<Post> postList=postRepository.findByUserId(userId);
        return postList
                .stream()
                .map((element)->modelMapper.map(element,PostDto.class))
                .collect(Collectors.toList());
    }
}
