package com.post.repository;

import com.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PostRepository extends ReactiveMongoRepository<Post, String> {
    Flux<Post> findAllByUserId(String userId);
    Flux<Post> findAllByUserId(String userId, Pageable pageable);
    Mono<Long> countByUserId(String userId);
}
