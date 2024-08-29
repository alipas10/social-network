package com.profile.mapper;

import org.mapstruct.Mapper;

import com.profile.dto.request.ProfileCreationRequest;
import com.profile.dto.response.UserProfileReponse;
import com.profile.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileReponse toUserProfileReponse(UserProfile entity);
}
