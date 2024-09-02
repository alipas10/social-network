package com.profile.service;

import com.profile.exception.AppException;
import com.profile.exception.ErrorCode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.profile.dto.request.ProfileCreationRequest;
import com.profile.dto.response.UserProfileResponse;
import com.profile.entity.UserProfile;
import com.profile.mapper.UserProfileMapper;
import com.profile.repository.UserProfileRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        userProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.toUserProfileReponse(userProfile);
    }

    public UserProfileResponse getProfile(String id) {
         var user = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
         return user.stream().filter(authority -> {
             return authority.getAuthority().equals("ROLE_ADMIN");
         }).findFirst()
                 .map(es -> {
            return  userProfileRepository.findById(id)
                    .map(userProfileMapper::toUserProfileReponse)
                    .orElseThrow( () -> new AppException(ErrorCode.USER_NOT_EXISTED));
                 })
                 .orElseGet( () -> {
                     log.info("another role");
                     return userProfileRepository.findByIdAndAllowedFindIsTrue(id)
                             .map(userProfileMapper::toUserProfileReponse)
                             .orElse(null);
                 });
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserProfileResponse> getAllProfiles() {
        var profiles = userProfileRepository.findAll();

        return profiles.stream().map(userProfileMapper::toUserProfileReponse).toList();
    }
}
