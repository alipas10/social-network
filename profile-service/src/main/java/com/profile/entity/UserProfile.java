package com.profile.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Node("user_profile")
@ToString
public class UserProfile {
    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    String id;

    @Property("userId")
    String userId;

    String firstName;
    String lastName;
    LocalDate dob;
    String city;
    Boolean allowedFind;

    @Relationship(type = "ADD_FRIEND_PENDING", direction = Relationship.Direction.INCOMING)
    Set<UserProfile> listFriendInvitation;

    @Relationship(type = "FRIEND_WITH")
    Set<UserProfile> friends;
}
