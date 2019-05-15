package com.georent.controller;

import com.georent.service.GeoRentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/*
41-я
Create lot controller and service methods to save new lot and show user lots
GeoRentUserController -> /user/lot and /user/lot/{id} endpoints

42-я
Create controller and service methods to retrieve user info, delete user, delete lot and delete all lots.
 */


@Controller
@RequestMapping("user")
public class GeoRentUserController {

    private final GeoRentUserService userService;

    @Autowired
    public GeoRentUserController(final GeoRentUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<String> getSecretResponse(Principal principal){
        String name = principal.getName();
        return ResponseEntity.ok(String.format("Hello %s.", name));
    }

    @GetMapping("/lot")
    public ResponseEntity<?> getUserLots(Principal principal){
        return ResponseEntity.ok(userService.getUserLots(principal));
    }

    @GetMapping("/lot/{id}")
    public ResponseEntity<?> getUserLotId(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.getUserLotId(principal, lotId));
    }

}