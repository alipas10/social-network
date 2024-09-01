package com.post.service;

import com.post.dto.request.PostRequest;
import com.post.dto.response.PageResponse;
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
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

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

    public Mono<PageResponse> getMyPosts(int page, int size, String fieldSort, String with){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Sort sort = with.equals("desc") ? Sort.by(fieldSort).descending():
                    Sort.by(fieldSort).ascending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        return postRepository.findAllByUserId(userId, pageable)
                .collectList()
                .zipWith(postRepository.countByUserId(userId))
                .map(p -> {
                 return  new PageImpl<>(p.getT1(),pageable, p.getT2());
                })
                .map(p -> {return PageResponse.builder()
                        .currentPage(page)
                        .pageSize(p.getSize())
                        .totalElements(p.getTotalElements())
                        .totalPages(p.getTotalPages())
                        .data(p.getContent().stream().map(postMapper::toPostResponse).toList())
                .build();})
                .doOnError(throwable -> {
                    log.info(throwable.getMessage());
                    throw new AppException(ErrorCode.CANNOT_SAVE_GET_ALL_POST);
                });
    }

}
