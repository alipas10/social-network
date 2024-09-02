package com.profile.mapper;

import org.mapstruct.Mapper;

import com.profile.dto.request.ProfileCreationRequest;
import com.profile.dto.response.UserProfileResponse;
import com.profile.entity.UserProfile;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    @Mapping(target = "listInvitation", source = "listFriendInvitation")
    UserProfileResponse toUserProfileReponse(UserProfile entity);
}
