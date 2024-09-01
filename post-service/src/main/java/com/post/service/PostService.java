package com.post.service;

import com.post.dto.request.PostRequest;
import com.post.dto.response.PostResponse;
import com.post.entity.Post;
import com.post.exception.AppException;
import com.post.exception.ErrorCode;
import com.post.mapper.PostMapper;
import com.post.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {

    PostMapper postMapper;
    PostRepository postRepository;

    public Mono<PostResponse> createPost(PostRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Post post = Post.builder()
                .content(request.getContent())
                .userId(authentication.getName())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .build();


        return postRepository.insert(post)
                .map(postMapper::toPostResponse)
                .doOnError(error -> {
                    log.debug(error.getMessage());
                    throw new AppException(ErrorCode.CANNOT_SAVE_POST);
                });
    }

    public Mono<List<PostResponse>> getMyPosts(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return postRepository.findAllByUserId(userId)
                .map(postMapper::toPostResponse)
                .collectList()
                ;
    }

}
