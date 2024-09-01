package com.notification.controller;

import com.event.NotificationEvent;
import com.notification.dto.request.SenRecEmail;
import com.notification.dto.request.SendEmailRequest;
import com.notification.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    EmailService emailService;

    @KafkaListener(topics = "notification-delivery", groupId = "group-send-email-greety")
    public void listenNotificationDelivery(NotificationEvent message){
        log.info("Message received: {}", message.toString());
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .to(SenRecEmail.builder()
                        .email(message.getRecipient())
                        .build())
                .subject(message.getSubject())
                .content(message.getBody())
                .build();
        emailService.sendEmail(sendEmailRequest);
    }
}
