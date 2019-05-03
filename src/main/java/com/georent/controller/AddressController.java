package com.georent.controller;

import com.georent.domain.Address;
import com.georent.dto.AddressDTO;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        address.setId(RandomUtils.nextLong());
        address.setUserId(RandomUtils.nextLong());
        address.setCoordId(RandomUtils.nextLong());
        // TODO set user and coordinates
        return address;
    }

    private AddressDTO mapToDto(Address address){
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setUserId(address.getUserId());
        // TODO addressDTO.setCoordinatesDTO();
        return addressDTO;
    }

}
