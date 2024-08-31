package com.profile.configuration;

import feign.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorizationEmailRequest implements RequestInterceptor, ResponseInterceptor {
    @Value( "${email.public-key}")
    String USER_NAME ;

    @Value("${email.secret-key}")
    String PASSWORD ;

    @Override
    public void apply(RequestTemplate template) {
        String base64Auth =" Basic " + Base64.encodeBase64String((USER_NAME + ":" + PASSWORD).getBytes());
        template.header(HttpHeaders.AUTHORIZATION, base64Auth);
        log.info(template.headers().toString());

    }

    @Override
    public Object intercept(InvocationContext invocationContext, Chain chain) throws Exception {
        return null;
    }

    @Override
    public ResponseInterceptor andThen(ResponseInterceptor nextInterceptor) {

        return ResponseInterceptor.super.andThen(nextInterceptor);
    }

    @Override
    public Chain apply(Chain chain) {
        return ResponseInterceptor.super.apply(chain);
    }
}
