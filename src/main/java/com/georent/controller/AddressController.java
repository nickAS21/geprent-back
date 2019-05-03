package com.georent.controller;

import com.georent.domain.Address;
import com.georent.dto.AddressDTO;
import com.georent.service.AddressService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Random;

/**
 * This is test controller.
 * When you run server it will listen to requests to endpoint mentioned in
 * {@link RequestMapping} annotation.
 * In this example this endpoint will be available: http://localhost:8080/address-controller
 * {@link GetMapping} shows that marked method will handle GET requests to http://localhost:8080/address-controller/*
 */
@Controller
@RequestMapping("address-controller")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * This method handles GET requests to endpoint: http://localhost:8080/address-controller/domain
     *
     * @return object of {@link Address} class wrapped with {@link ResponseEntity}
     */
    @GetMapping("/domain")
    public ResponseEntity<Address> getAddress(){
        Address address = generateAddress();
        return ResponseEntity.ok().body(address);
    }

    @GetMapping("/domain/{id}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long id) {
        Address address = addressService.getById(id);
        return ResponseEntity.ok().body(mapToDto(address));
    }

    @PostMapping("/domain")
    public ResponseEntity<AddressDTO> saveAddress(@RequestBody AddressDTO addressDTO) {
        Address address = mapFromDto(addressDTO);
        Address savedAddress = addressService.save(address);
        return ResponseEntity.ok().body(mapToDto(savedAddress));
    }

    @DeleteMapping("/domain/{id}")
    public ResponseEntity<String> deleteAddressById(@PathVariable Long id) {
        addressService.deleteById(id);
        return ResponseEntity.ok().body("Address with id: " + id + " deleted");
    }

    /**
     * This method handles GET requests to endpoint: http://localhost:8080/address-controller/dto
     * @return object of {@link AddressDTO} class wrapped with {@link ResponseEntity}
     */
    @GetMapping("/dto")
    public ResponseEntity<AddressDTO> getAddressDTO(){
        Address address = generateAddress();
        return ResponseEntity.ok().body(mapToDto(address));
    }

    private Address generateAddress() {
        Address address = new Address();
        address.setId(RandomUtils.nextLong(1, 100));
        address.setUserId(RandomUtils.nextLong(1, 100));
        address.setCoordId(RandomUtils.nextLong(1, 100));
        // TODO set user and coordinates
        return address;
    }

    private AddressDTO mapToDto(Address address){
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setUserId(address.getUserId());
        addressDTO.setCoordId(address.getCoordId());
        return addressDTO;
    }

    private Address mapFromDto(AddressDTO addressDTO) {
        Address address = new Address();
        address.setId(addressDTO.getId());
        address.setUserId(addressDTO.getUserId());
        address.setCoordId(addressDTO.getCoordId());
        return address;
    }

}
