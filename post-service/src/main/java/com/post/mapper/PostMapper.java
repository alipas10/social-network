package com.post.mapper;

import com.post.dto.response.PostResponse;
import com.post.entity.Post;
import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
