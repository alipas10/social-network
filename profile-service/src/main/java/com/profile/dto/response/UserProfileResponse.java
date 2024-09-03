package com.profile.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileResponse {
    String id;
    String firstName;
    String lastName;
    LocalDate dob;
    String city;
    Set<UserProfileResponse> listInvitation;
    Set<UserProfileResponse> friends;

}
