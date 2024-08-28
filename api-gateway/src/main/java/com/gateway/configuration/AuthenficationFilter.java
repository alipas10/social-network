package com.gateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class AuthenficationFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;

    public AuthenficationFilter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.print("enter authentication filter");
        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return reponseUnauthorization(exchange.getResponse());
        String jwt = authHeader.getFirst().replace("Bearer ", "");

        return webClient.get().uri("http://localhost:8080/auth/verifyt-token/{jwt}",jwt)
                .retrieve().toBodilessEntity()

                .flatMap( x -> {
                    if(x.getStatusCode().is2xxSuccessful()) {
                        return chain.filter(exchange);
                    }
                    else{
                        System.out.println(x.getStatusCode());
                        return reponseUnauthorization(exchange.getResponse());
                    }
                })
                .onErrorResume(throwable -> reponseUnauthorization(exchange.getResponse()))
                ;

    }

    @Override
    public int getOrder() {
        return -1;
    }

    Mono<Void> reponseUnauthorization (ServerHttpResponse response){
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap("Unauthenticated".getBytes())));
    }
}
