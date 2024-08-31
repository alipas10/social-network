package com.profile.service;

import com.profile.dto.request.EmailRequest;
import com.profile.dto.request.Messages;
import com.profile.dto.request.SenRecEmail;
import com.profile.dto.request.SendEmailRequest;
import com.profile.dto.response.EmailResponse;
import com.profile.exception.AppException;
import com.profile.exception.ErrorCode;
import com.profile.repository.httpclient.EmailClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.stereotype.Service;

import java.util.List;

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
