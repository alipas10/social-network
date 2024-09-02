package com.profile.controller;

import com.profile.dto.response.ApiResponse;
import com.profile.dto.response.PageResponse;
import com.profile.dto.response.UserProfileResponse;
import com.profile.service.UserProfileService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping(path = "/users")
public class UserProfileController {
    UserProfileService userProfileService;

    @GetMapping("/{profileId}")
    ApiResponse<UserProfileResponse> getProfile(@PathVariable String profileId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getProfile(profileId))
                .build();
    }

    @GetMapping()
    ApiResponse<PageResponse<UserProfileResponse>> getAllProfiles(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "by", required = false, defaultValue = "dob") String sortBy,
            @RequestParam(value = "type", required = false, defaultValue = "desc") String sortType
    ) {
        return ApiResponse.<PageResponse<UserProfileResponse>>builder()
                .result(userProfileService.getAllProfiles(page, size, sortBy, sortType))
                .build();
    }

    @GetMapping("/send-invitation/{idTarget}")
    ApiResponse<UserProfileResponse> sendInvitation(@PathVariable String idTarget) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.sendInvitation(idTarget))
                .build();
    }
}
