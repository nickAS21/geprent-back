package com.georent.controller;

import com.georent.service.RentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("rent")
public class RentOrderOwnerController {

    private final RentOrderService rentOrderService;

    @Autowired
    public RentOrderOwnerController(RentOrderService rentOrderService) {
        this.rentOrderService = rentOrderService;
    }

    /**
     * Processes the GET request to "user/order" URI.
     *
     * @return Response, containing the list of all orders for this owner lots.
     */
    @GetMapping("user/order")
    public ResponseEntity<?> getAllRentOrders(Long ownerId) {
        return ResponseEntity.ok(rentOrderService.getRentOrdersDto(ownerId));
    }

    /**
     * Processes the GET request to "/user/order/{orderId" URI.
     *
     * @param orderId the id of the rentOrder, specified in the request path.
     * @return Response, containing the requested rentOrder in the format of RentOrderDTO.
     */
    @GetMapping("/user/order/{orderId}")
    public ResponseEntity<?> getRentOrderById(@PathVariable(value = "orderId") Long orderId) {
        return status(OK).body(rentOrderService.getRentOrderDtoById(orderId));
    }

    /**
     * Processes the GET request to "/user/lot/{lotId}/order" URI.
     *
     * @param lotId the id of lot specified in the request path.
     * @return Response, containing the requested rentOrder in the format of RentOrderDTO.
     */
    @GetMapping("/user/lot/{lotId}/order")
    public ResponseEntity<?> getRentOrdersByLotId(@PathVariable(value = "lotId") Long lotId) {
        return status(OK).body(rentOrderService.getRentOrdersDtoByLotId(lotId));
    }

    /**
     * Processes PATCH requests to endpoint "/user/order/{orderId}".
     * Updates rentOrder information in the database.
     * @param orderId the id of the rentOrder, specified in the request path.
     * @param status updated status in rent order.
     * @return Response, containing the updated user information in the format of RentOrderDTO.
     */
//    @PatchMapping("/user/order/{orderId}")
//    public ResponseEntity<?> getOrderId(@PathVariable(value = "orderId") Long orderId, RentOrderStatus status) {
//        return status(OK).body(rentOrderService.updateStatusInRentOrder(orderId, status));
//    }

    /**
     * Processes DELETE requests to endpoint "/user/order".
     * Deletes all orders for the lots of this owner
     * Response, containing the proper message.
     */
    @DeleteMapping("/user/order")
    public ResponseEntity<?> delete(long OwnerId){
        return ResponseEntity.ok("");
    }

    /**
     * Processes DELETE requests to endpoint "/user/order/{orderId}".
     * Deletes the specified order from the database.
     * @param orderId The id of the specified order.
     * @return Response, containing the proper message.
     */
//    @DeleteMapping ("/user/order/{orderId}")
//    public ResponseEntity<?> deleteRentOrderById(@PathVariable(value = "orderId") Long orderId){
//        return ResponseEntity.ok(rentOrderService.deleteRentOrderById(orderId));
//    }

    /**
     * Processes DELETE requests to endpoint "/user/order/{orderId}".
     * Deletes all orders of specified lot from the database.
     * @param lotId The id of the specified lot.
     * @return Response, containing the proper message.
     */
    @DeleteMapping ("/user/lot/{lotId}/order")
    public ResponseEntity<?> deleteRentOrdersByLotId(@PathVariable(value = "lotId") Long lotId){
        return ResponseEntity.ok("");
    }


}
