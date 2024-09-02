package com.profile.service;

import com.profile.dto.response.PageResponse;
import com.profile.exception.AppException;
import com.profile.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;

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
    public PageResponse<UserProfileResponse> getAllProfiles(
            Integer page, Integer size, String sortBy, String sortType
    ) {
        Sort sort ="desc".equals(sortType) ? Sort.by(sortBy).descending():
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page -1, size, sort);
        var profiles = userProfileRepository.findAll(pageable);

        return PageResponse.<UserProfileResponse>builder()
                .currentPage(page)
                .pageSize(profiles.getSize())
                .totalElements(profiles.getTotalElements())
                .totalPages(profiles.getTotalPages())
                .data(profiles.getContent().stream()
                        .map(userProfileMapper::toUserProfileReponse).toList())
                .build();
    }
}
