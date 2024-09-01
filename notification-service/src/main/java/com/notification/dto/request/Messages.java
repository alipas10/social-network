package com.notification.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Messages {
    SenRecEmail from;
    List<SenRecEmail> to;
    String subject;
    String textPart;
}
