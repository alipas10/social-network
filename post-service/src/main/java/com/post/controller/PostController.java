package com.post.controller;

import com.post.dto.request.PostRequest;
import com.post.dto.response.ApiResponse;
import com.post.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
    PostService postService;

    @PostMapping("/create")
    Mono<ApiResponse<Object>> createPost(@RequestBody PostRequest request){
        return postService.createPost(request)
                .map( postResponse -> {
                    return ApiResponse.builder()
                            .result(postResponse)
                            .build();
                });
    }

    @GetMapping("/my-posts")
    Mono<ApiResponse<Object>> myPosts(){
        return postService.getMyPosts()
                .map( list -> {
                    return ApiResponse.builder()
                            .result(list)
                            .build();
                });
    }
}
