package com.profile.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessagesEmailResponse {
    String status;
    String customID;
    List<MessageToResponse> to;
    List<String> cc;
    List<String> bcc;
}
