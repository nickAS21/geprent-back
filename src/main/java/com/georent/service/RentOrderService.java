package com.georent.service;

import com.georent.domain.RentOrder;
import com.georent.domain.RentOrderStatus;
import com.georent.repository.RentOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;


public class RentOrderService {

    private final RentOrderRepository rentOrderRepository;

    @Autowired
    public RentOrderService(RentOrderRepository rentOrderRepository) {
        this.rentOrderRepository = rentOrderRepository;
    }

    public Object getRentOrdersDto(long OwnerId) {
        return rentOrderRepository.findAllByOwnerId(OwnerId);
    }

    public Object getRentOrderDtoById(Long orderId) {
        return rentOrderRepository.findById(orderId);
    }

    public Object getRentOrdersDtoByLotId(Long lotId) {
        return rentOrderRepository.findAllByLotId(lotId);
    }

//    public Object updateStatusInRentOrder(Long orderId, RentOrderStatus status) {
//        RentOrder orderToUpdate = rentOrderRepository.getRentOrderById(orderId);
//        orderToUpdate.setStatus(status);
//
//    }

    public Object deleteAllRentOrders(long OwnerId) {
        return rentOrderRepository.deleteAllByOwnerId(OwnerId);
    }

//    public Object deleteRentOrderById(Long orderId) {
//        return rentOrderRepository.deleteById(orderId);
//    }

    public Object deleteRentOrdersByLotId(Long lotId) {
        return rentOrderRepository.deleteAllByLotId(lotId);
    }
}
