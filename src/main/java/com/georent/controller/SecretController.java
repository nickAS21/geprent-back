package com.georent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/secret")
public class SecretController {

    @GetMapping
    public ResponseEntity<String> getSecretResponse(Principal principal){
        String name = principal.getName();
        return ResponseEntity.ok(String.format("Hello %s. Here is your secret!", name));
    }
}
