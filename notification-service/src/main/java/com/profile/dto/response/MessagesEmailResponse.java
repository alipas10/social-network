package com.profile.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagesEmailResponse {
    @JsonProperty(value = "Status" )
    String status;

    @JsonProperty(value = "CustomID" )
    String customID;

    @JsonProperty(value = "To" )
    List<MessageToResponse> to;

    @JsonProperty(value = "cc" )
    List<String> cc;

    @JsonProperty(value = "bcc" )
    List<String> bCc;

}
