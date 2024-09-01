package com.notification.repository.httpclient;

import com.notification.configuration.AuthorizationEmailRequest;
import com.notification.dto.request.EmailRequest;
import com.notification.dto.response.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-client", url = "https://api.mailjet.com",
    configuration = AuthorizationEmailRequest.class)
public interface EmailClient {
    @PostMapping(value = "/v3.1/send", produces = MediaType.APPLICATION_JSON_VALUE)
    EmailResponse sendEmail(@RequestBody EmailRequest body);
}
