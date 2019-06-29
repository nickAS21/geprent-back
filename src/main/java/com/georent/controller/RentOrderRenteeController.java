package com.georent.controller;

import com.georent.dto.RentOrderDTO;
import com.georent.dto.RentOrderRequestDTO;
import com.georent.service.RentOrderRenteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("rent/rentee")
public class RentOrderRenteeController {

//    POST	    rentee/order	            Initial posting of the order (registration)
//    GET	    rentee/order	            Get all orders, submitted by this rentee
//    GET	    rentee/order/{orderId}	    Get this order of this rentee
//    GET	    rentee/order/lot/{lotId}    Get all orders for this lot by this rentee.
//    PATCH	    rentee/order/{orderId}	    Edit dates in this order with PENDING
//    DELETE	rentee/order	            Delete all orders of this rentee
//    DELETE	rentee/order/{orderId}	    Delete this order
//    DELETE	rentee/order/lot/{lotId}	Delete all orders for this lot

    RentOrderRenteeService rentOrderRenteeService;

    @Autowired
    public RentOrderRenteeController(RentOrderRenteeService rentOrderRenteeService) {
        this.rentOrderRenteeService = rentOrderRenteeService;
    }

    /**
     * Processes POST requests to the endpoint "rent/rentee/order"
     * Saves the provided order to the database.
     * @param orderRequestDTO The order to save to the database in RentOrderRequestDTO format.
     * @param principal Current user identifier.
     * @return Response, containing the saved order in the RentOrderDTO format.
     */
    @PostMapping("/order")
    public ResponseEntity<?> saveRentOrder(
            @RequestBody final RentOrderRequestDTO orderRequestDTO,
            Principal principal){
        return ResponseEntity.ok(rentOrderRenteeService.saveRentOrder(principal, orderRequestDTO));
    }

    /**
     * Processes GET requests to the endpoint "rent/rentee/order"
     * Retrieves the list of user (rentee) orders from the database.
     * @param principal Current user (rentee) identifier
     * @return response, containing the list
     * of user (rentee) orders in the RentOrderDTO format.
     */
    @GetMapping("/order")
    public ResponseEntity<?> getRenteeOrders(Principal principal){
        return ResponseEntity.ok(rentOrderRenteeService.getRenteeOrders(principal));
    }

    /**
     * Processes GET requests to the endpoint "rent/rentee/order/{id}"
     * Reads the order with provided id from the database.
     * Checks if this user has the access to this order,
     * if not, throws OrderNotFoundException.
     * @param orderId the id of the order.
     * @param principal current user (rentee) identifier.
     * @return response, containing the order in the RentOrderDTO format.
     */
    @GetMapping("/order/{id}")
    public ResponseEntity<?> getRenteeOrderById(
            @PathVariable(value = "orderId") Long orderId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderRenteeService.getRenteeOrderById(orderId, principal));
    }

    /**
     * Processes GET requests to the endpoint "rent/rentee/order/lot/{id}"
     * Reads the list of user (rentee) orders to the specified lot from the database.
     * @param lotId lot identifier.
     * @param principal Current user (rentee) identifier.
     * @return response, containing the list of user (rentee) orders in the RentOrderDTO format.
     */
    @GetMapping("/order/lot/{id}")
    public ResponseEntity<?> getRenteeOrdersToLot(
            @PathVariable(value = "lotId") Long lotId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderRenteeService.getRenteeOrdersToLot(lotId, principal));
    }

    /**
     * Processes PATCH requests to the endpoint "rent/rentee/order/{id}"
     * Updates start and end dates in the order with provided id.
     * Checks if this user has the access to this order.
     * If not, throws OrderNotFoundException.
     * Checks if this order status is pending.
     * If not, throws RentOrderUpdateException.
     * @param orderId the id of the order to update.
     * @param updateOrderDTO - the new values.
     * @param principal - user (rentee) identifier.
     * @return response, containing the updated order.
     */
    @PatchMapping("/order/{id}")
    public ResponseEntity<?> patchRenteeOrderById(
            @PathVariable(value = "orderId") Long orderId,
            @RequestBody final RentOrderDTO updateOrderDTO,
            Principal principal) {
        return ResponseEntity.ok(rentOrderRenteeService
                .patchRenteeOrderById(orderId, updateOrderDTO ,principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/rentee/order"
     * Deletes all the orders of this user.
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/order")
    public ResponseEntity<?> deleteRenteeOrders(Principal principal){
        return ResponseEntity.ok(rentOrderRenteeService.deleteRenteeOrders(principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/rentee/order/{id}"
     * Deletes the order with provided id from the database.
     * Checks if this user has the access to this order.
     * If not, throws OrderNotFoundException.
     * @param orderId the id of the order to delete.
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/order/{id}")
    public ResponseEntity<?> deleteRenteeOrderById(
            @PathVariable(value = "orderId") Long orderId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderRenteeService.deleteRenteeOrderById(orderId, principal));
    }

    /**
     * Processes DELETE requests to the endpoint "rent/rentee/order/lot/{id}"
     * Deletes all the orders of this user to the lot with provided id.
     * @param lotId the id of the lot, from which to delete the orders
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    @DeleteMapping("/order/lot/{id}")
    public ResponseEntity<?> deleteRenteeOrderToLot(
            @PathVariable(value = "lotId") Long lotId,
            Principal principal) {
        return ResponseEntity.ok(rentOrderRenteeService.deleteRenteeOrdersToLot(lotId, principal));
    }
}
