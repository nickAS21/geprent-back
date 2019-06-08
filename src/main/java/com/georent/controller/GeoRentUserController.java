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
import java.security.Principal;

/**
 * Request controllers to the user and lot endpoints, that do require authentication.
 */
@Controller
@RequestMapping("user")
public class GeoRentUserController {

    private final GeoRentUserService userService;

    @Autowired
    public GeoRentUserController(final GeoRentUserService userService) {
        this.userService = userService;
    }

    /**
     * Processes GET requests to endpoint "/user".
     * Returns the user information from the database.
     * @param principal current user identifier.
     * @return Response, containing the user information in the format of GeoRentUserInfoDTO.
     */
    @GetMapping
    public ResponseEntity<?> getUserInfo(Principal principal){
        return ResponseEntity.ok(userService.getUserInfo(principal));
    }

    /**
     * Processes PATCH requests to endpoint "/user".
     * Updates user information in the database.
     * @param geoRentUserUpdateDto - User information to update in the format of GeoRentUserUpdateDto.
     * @param principal current user identifier.
     * @return Response, containing the updated user information in the format of GeoRentUserUpdateDto.
     */
    @PatchMapping
    public ResponseEntity<?> updateUser(@RequestBody GeoRentUserUpdateDto geoRentUserUpdateDto, Principal principal) {
        return ResponseEntity.ok(userService.updateUser(principal, geoRentUserUpdateDto));
    }

    /**
     * Processes DELETE requests to endpoint "/user".
     * Permanently deletes the user, user lots, and all relative info from the database.
     * @param principal current user identifier.
     * @return Response, containing the user information in the format of GeoRentUserInfoDTO.
     */
    @DeleteMapping
    public ResponseEntity<?> deletetUser(Principal principal){
        return ResponseEntity.ok(userService.deleteUser(principal));
    }

    /**
     * Processes GET requests to endpoint "/lots".
     * Reads the list of user lots from the database.
     * @param principal current user identifier.
     * @return Response, containing the list of user lots in the format of LotDTO.
     */
    @GetMapping("/lots")
    public ResponseEntity<?> getUserLots(Principal principal){
        return ResponseEntity.ok(userService.getUserLots(principal));
    }

    /**
     * Processes GET requests to endpoint "/lots/{id}".
     * Reads the user lot with specified id from the database.
     * @param principal Current user identifier.
     * @param lotId The id of the specified lot.
     * @return Response, containing the requested lot in the format of LotDTO.
     */
    @GetMapping("/lot/{id}")
    public ResponseEntity<?> getUserLotId(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.getUserLotId(principal, lotId));
    }

    /**
     * Processes GET requests to endpoint "/lot/upload-picture/{id}".
     * Downloads the requested lot picture from the pic repository to temp file.
     * @param lotId The id of the requested lot.
     * @param principal Current user identifier.
     * @return Response, containing the lot with specified id in the format of LotDTO.
     */
    @GetMapping("/lot/upload-picture/{id}")
    public ResponseEntity<?> getUploadPicture(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.getUserLotIdUploadPicture(principal, lotId));
    }

    /**
     * Processes POST requests to the endpoint "/lot"
     * Saves the provided lot to the database.
     * @param registrationLotDto The lot to save to the database in RegistrationLotDto format.
     * @param principal Current user identifier.
     * @return Response, containing the saved lot in the LotDTO format.
     */
    @PostMapping("/lot")
    public ResponseEntity<?> setUserLot(@Valid @RequestBody final RegistrationLotDto registrationLotDto, Principal principal){
        return ResponseEntity.ok(userService.saveUserLot(principal, registrationLotDto));
    }

    /**
     * Processes POST requests to the endpoint "/lot/upload-picture".
     * Uploads the lot picture to the pictures repository.
     * @param multipartFile The file to upload.
     * @param registrationLotDtoStr ???
     * @param principal Current user identifier.
     * @return ???
     */
    @PostMapping("/lot/upload-picture")
    public ResponseEntity<?> setUploadPicture(@Valid @RequestParam(name = "file") MultipartFile multipartFile,
                                              @RequestParam(name = "testDto") String registrationLotDtoStr,
                                              Principal principal)  {
        return userService.saveUserLotUploadPicture(multipartFile, principal, registrationLotDtoStr);
    }

    /**
     * Processes DELETE requests to endpoint "/lots/{id}".
     * Deletes the specified lot from the database.
     * @param lotId The id of the specified lot.
     * @param principal Current user identifier.
     * @return Response, containing the proper message.
     */
    @DeleteMapping ("/lot/{id}")
    public ResponseEntity<?> deletetLotId(@PathVariable(value = "id") Long lotId, Principal principal){
        return ResponseEntity.ok(userService.deleteteUserLotId(principal, lotId));
    }

    /**
     * Processes DELETE requests to endpoint "/lots".
     * Deletes all the user lots from the database.
     * @param principal Current user identifier.
     * @return Response, containing the proper message.
     */
    @DeleteMapping ("/lots")
    public ResponseEntity<?> deletetLots(Principal principal){
        return ResponseEntity.ok(userService.deleteteUserLotAll(principal));
    }

}