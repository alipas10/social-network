package com.profile.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageToResponse {
    @JsonProperty(value = "Email" )
    String email;

    @JsonProperty(value = "MessageUUID" )
    String messageUUID;

    @JsonProperty(value = "MessageID" )
    String messageID;

    @JsonProperty(value = "MessageHref" )
    String messageHref;
}
