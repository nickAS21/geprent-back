package com.georent.controller;

import com.georent.dto.GeoRentUserUpdateDto;
import com.georent.dto.RegistrationLotDto;
import com.georent.service.GeoRentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;

@Controller
@RequestMapping("user")
public class GeoRentUserController {

    private final GeoRentUserService userService;

    @Autowired
    public GeoRentUserController(final GeoRentUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUserInfo(Principal principal){
        return ResponseEntity.ok(userService.getUserInfo(principal));
    }

    @PatchMapping
    public ResponseEntity<?> updateUser(@RequestBody GeoRentUserUpdateDto geoRentUserUpdateDto, Principal principal) {
        return ResponseEntity.ok(userService.updateUser(principal, geoRentUserUpdateDto));
    }

    @DeleteMapping
    public ResponseEntity<?> deletetUser(Principal principal){
        return ResponseEntity.ok(userService.deleteUser(principal));
    }

    @GetMapping("/lots")
    public ResponseEntity<?> getUserLots(Principal principal){
        return ResponseEntity.ok(userService.getUserLots(principal));
    }

    @GetMapping("/lot/{id}")
    public ResponseEntity<?> getUserLotId(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.getUserLotId(principal, lotId));
    }

    @PostMapping("/lot")
    public ResponseEntity<?> setUserLot(@Valid @RequestBody final RegistrationLotDto registrationLotDto, Principal principal){
        return ResponseEntity.ok(userService.saveUserLot(principal, registrationLotDto));
    }

    @PostMapping("/lot/upload")
    public String setUserLotUpload(@Valid @RequestBody @RequestParam("file") MultipartFile file){
        String name = "test11";
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

    @DeleteMapping ("/lot/{id}")
    public ResponseEntity<?> deletetLotId(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.deleteteUserLotId(principal, lotId));
    }

    @DeleteMapping ("/lots")
    public ResponseEntity<?> deletetLots(Principal principal){
        return ResponseEntity.ok(userService.deleteteUserLotAll(principal));
    }

}