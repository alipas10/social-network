package com.profile.service;

import com.event.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profile.dto.response.PageResponse;
import com.profile.exception.AppException;
import com.profile.exception.ErrorCode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
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

import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    ObjectMapper objectmapper;
    KafkaTemplate<String,Object> kafkaTemplate;

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


    public UserProfileResponse sendInvitation(String idTarget) {
        UserProfile userTarget = userProfileRepository.findByUserId(idTarget)
                .orElseThrow( () -> {
                            throw new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        var userId = SecurityContextHolder.getContext().getAuthentication()
                .getName();
        UserProfile currentUser = userProfileRepository.findByUserId(userId)
                .orElseThrow( () -> {
                   return new AppException(ErrorCode.USER_NOT_EXISTED);
                });

        var checkListInvition  = currentUser.getListFriendInvitation().stream()
                        .anyMatch(invUser -> {
                            return invUser.getUserId().equals(userTarget.getUserId());
                        });

        if(!checkListInvition){
            NotificationEvent notificationEvent = NotificationEvent.builder()
                    .channel("FCM")
                    .recipient(userTarget.getUserId())
                    .subject(currentUser.getFirstName() + currentUser.getLastName() +
                            " send friend invitation to you ")
                    .param(objectmapper.convertValue(currentUser, Map.class))
                    .build();
            log.info("Before send notification {}",notificationEvent.toString());
            try{
                kafkaTemplate.send("notification-invitation", notificationEvent);
            } catch (KafkaException e ){
                log.error("Dont send notification to Id: {}",userTarget.getUserId() );
                throw new AppException( ErrorCode.UNCATEGORIZED_EXCEPTION);
            }

            var listInvitation = currentUser.getListFriendInvitation();
            listInvitation. add(userTarget);
            currentUser.setListFriendInvitation(listInvitation);

            return  userProfileMapper.toUserProfileReponse(
                    userProfileRepository.save(currentUser));
        }else
            throw new AppException(ErrorCode.USER_INVITED);

    }
}
