package com.post.controller;

import com.post.dto.request.PostRequest;
import com.post.dto.response.ApiResponse;
import com.post.dto.response.PageResponse;
import com.post.dto.response.PostResponse;
import com.post.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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

    @GetMapping("/my-posts-pagging")
    Mono<ApiResponse<Object>> myPosts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sortBy", required = false, defaultValue = "createdDate") String fieldSort,
            @RequestParam(value = "with", required = false, defaultValue = "desc") String with
    ){

        return postService.getMyPosts(page, size, fieldSort, with)
                .map(p -> ApiResponse.builder()
                        .result(p)
                        .build());

    }
}
