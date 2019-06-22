package com.georent.service;

import com.georent.domain.RentOrder;
import com.georent.domain.RentOrderStatus;
import com.georent.repository.RentOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class RentOrderService {

    private final RentOrderRepository rentOrderRepository;

    @Autowired
    public RentOrderService(RentOrderRepository rentOrderRepository) {
        this.rentOrderRepository = rentOrderRepository;
    }

    public Object getRentOrdersDto(Long ownerId) {
        return rentOrderRepository.findAllById(Collections.singletonList(ownerId));
    }

    public Object getRentOrderDtoById(Long orderId) {
        return rentOrderRepository.findById(orderId);
    }

    public Object getRentOrdersDtoByLotId(Long lotId) {
        return rentOrderRepository.findAllById(Collections.singletonList(lotId));
    }

//    public Object updateStatusInRentOrder(Long orderId, RentOrderStatus status) {
//        RentOrder orderToUpdate = rentOrderRepository.getRentOrderById(orderId);
//        orderToUpdate.setStatus(status);
//
//    }

//    public Object deleteAllRentOrders(Long OwnerId) {
//        return rentOrderRepository.deleteAll(Collections.singletonList(OwnerId));
//    }
//
////    public Object deleteRentOrderById(Long orderId) {
////        return rentOrderRepository.deleteById(orderId);
////    }
//
//    public Object deleteRentOrdersByLotId(Long lotId) {
//        return rentOrderRepository.deleteAllByLotId(lotId);
//    }
}
