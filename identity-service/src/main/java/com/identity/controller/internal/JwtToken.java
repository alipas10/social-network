package com.identity.controller.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/auth")
public class JwtToken {

    @GetMapping(path = "/verifyt-token/{jwt}")
    public ResponseEntity<String> verifyToken(@PathVariable String jwt) {
        System.out.println("jwt = " + jwt);
        if ("testJWT".equals(jwt)) return ResponseEntity.ok("JWT valid");
        return ResponseEntity.badRequest().body("JWT unvalid");
    }
}
