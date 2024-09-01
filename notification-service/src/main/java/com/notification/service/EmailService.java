package com.notification.service;

import com.notification.dto.request.EmailRequest;
import com.notification.dto.request.Messages;
import com.notification.dto.request.SenRecEmail;
import com.notification.dto.request.SendEmailRequest;
import com.notification.dto.response.EmailResponse;
import com.notification.exception.AppException;
import com.notification.exception.ErrorCode;
import com.notification.repository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {
    EmailClient emailClient;

    public EmailResponse sendEmail(SendEmailRequest request) {
        EmailRequest emailRequest = EmailRequest.builder()
                .messages(List.of(Messages.builder()
                                .from(SenRecEmail.builder()
                                        .email("phuonkpdd@gmail.com")
                                        .name("Admin")
                                        .build())
                                .to(List.of(SenRecEmail.builder()
                                        .email(request.getTo().getEmail())
                                        .name(request.getTo().getName())
                                        .build()))
                                .subject(request.getSubject())
                                .textPart(request.getContent())
                        .build()))
                .build();
        try {
            return emailClient.sendEmail(emailRequest);
        } catch (FeignException e){
            throw new AppException(ErrorCode.CANNOT_SEND_EMAIL);
        }
    }
}
