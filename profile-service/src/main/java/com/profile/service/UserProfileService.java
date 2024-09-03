package com.profile.service;

import com.event.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profile.dto.response.PageResponse;
import com.profile.exception.AppException;
import com.profile.exception.ErrorCode;
import org.apache.catalina.User;
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
import java.util.Set;
import java.util.function.Predicate;

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
                     log.info("This is getProfile method role ADMIN");
                     return  userProfileRepository.findByUserId(id)
                        .map(userProfileMapper::toUserProfileReponse)
                        .orElseThrow( () -> new AppException(ErrorCode.USER_NOT_EXISTED));
                 })
                 .orElseGet( () -> {
                     log.info("another role");
                     return userProfileRepository.findByUserIdAndAllowedFindIsTrue(id)
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
                .orElseThrow( () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile currentUser = userProfileRepository.findByUserId(userId)
                .orElseThrow( () ->  new AppException(ErrorCode.USER_NOT_EXISTED));

        var lstInvitationCurrentUser = currentUser.getListFriendInvitation();
        Predicate<UserProfile> checkExistUser = invUser -> {
            return invUser.getUserId().equals(userTarget.getUserId());
        };

        boolean checkListInvition  = lstInvitationCurrentUser.stream()
                        .anyMatch(checkExistUser);

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

            var listInvitation = userTarget.getListFriendInvitation();
            listInvitation.add(currentUser);
            userTarget.setListFriendInvitation(listInvitation);

            return  userProfileMapper.toUserProfileReponse(
                    userProfileRepository.save(userTarget));
        }else {
            log.info("target user have already sended invitation ");
            Set<UserProfile> lstFriend = currentUser.getFriends();
            lstFriend.add(userTarget);
            currentUser.setFriends(lstFriend);

            lstInvitationCurrentUser.removeIf(checkExistUser);
            currentUser.setListFriendInvitation(lstInvitationCurrentUser);

            return userProfileMapper.toUserProfileReponse(
                    userProfileRepository.save(currentUser));
        }

    }

    public UserProfileResponse acceptInvitation(String idTarget) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        UserProfile currentUser = userProfileRepository.findByUserId(currentUserId)
                .orElseThrow( () -> {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                });
        Set<UserProfile> lstInvitation = currentUser.getListFriendInvitation();

        Predicate<UserProfile> checkExistUser = user -> {
            return user.getUserId().endsWith(idTarget);
        };

        UserProfile targetUser  = lstInvitation
                .stream().filter(checkExistUser)
                    .findFirst()
                .orElseThrow( () -> {throw new AppException(ErrorCode.USER_NOT_EXISTED) ;});


        boolean alreadyFriend = currentUser.getFriends()
                .stream().anyMatch(checkExistUser);
        if (!alreadyFriend) {
            log.info("not yet friend");
            Set<UserProfile> lstFriend = currentUser.getFriends();
            lstFriend.add(targetUser);
            currentUser.setFriends(lstFriend);

            lstInvitation.removeIf(checkExistUser);
            currentUser.setListFriendInvitation(lstInvitation);

            return userProfileMapper.toUserProfileReponse(
                    userProfileRepository.save(currentUser));
        }
        return userProfileMapper.toUserProfileReponse(currentUser);
    }
}
