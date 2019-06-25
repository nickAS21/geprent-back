package com.georent.controller;

import com.georent.dto.RegistrationLotDto;
import com.georent.dto.RentOrderRequestDTO;
import com.georent.service.RentOrderRenteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("rent/rentee")
public class RentOrderRenteeController {

//    POST	rentee/order	Initial posting of the order (registration)
//    GET	rentee/order	Get all orders, filed by this rentee
//    GET	rentee/order/{orderId}	Get this order of this rentee
//    GET	rentee/lot/{lotId}/order	Get all orders for this lot by this rentee.
//    PATCH	rentee/order/{orderId}	Edit dates in this order with PENDING
//    DELETE	rentee/order	Delete all orders of this rentee
//    DELETE	rentee/order/{orderId}	Delete this order
//    DELETE	rentee/lot/{lotId}/order	Delete all orders for this lot

    RentOrderRenteeService rentOrderRenteeService;

    @Autowired
    public RentOrderRenteeController(RentOrderRenteeService rentOrderRenteeService) {
        this.rentOrderRenteeService = rentOrderRenteeService;
    }

    /**
     * Processes POST requests to the endpoint "rentee/order"
     * Saves the provided lot to the database.
     * @param orderRequestDTO The order to save to the database in RentOrderRequestDTO format.
     * @param principal Current user identifier.
     * @return Response, containing the saved lot in the LotDTO format.
     */
    @PostMapping("/order")
    public ResponseEntity<?> createRentOrder(@RequestBody final RentOrderRequestDTO orderRequestDTO, Principal principal){
        return ResponseEntity.ok(rentOrderRenteeService.saveRentOrder(principal, orderRequestDTO));
    }

}
