package com.georent.service;

import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.domain.RentOrder;
import com.georent.domain.RentOrderStatus;
import com.georent.dto.GenericResponseDTO;
import com.georent.dto.LotDTO;
import com.georent.dto.RentOrderDTO;
import com.georent.dto.RentOrderRequestDTO;
import com.georent.message.Message;
import com.georent.repository.GeoRentUserRepository;
import com.georent.repository.LotRepository;
import com.georent.repository.RentOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;
import java.security.Principal;

public class RentOrderRenteeService {
    private final RentOrderRepository rentOrderRepository;
    private final GeoRentUserRepository userRepository;
    private final LotRepository lotRepository;

    @Autowired
    public RentOrderRenteeService(RentOrderRepository rentOrderRepository, GeoRentUserRepository userRepository, LotRepository lotRepository) {
        this.rentOrderRepository = rentOrderRepository;
        this.userRepository = userRepository;
        this.lotRepository = lotRepository;
    }

    /**
     * Saves the provided order to the database.
     * @param principal Current user (rentee) identifier.
     * @param orderRequestDTO The order to save in the RentOrderRequestDTO format.
     * @return The response with saved order in the RentOrderDTO format.
     */
    @Transactional
    public GenericResponseDTO<RentOrderDTO> saveRentOrder(
            Principal principal,
            final RentOrderRequestDTO orderRequestDTO) {

        Lot lot = findLotById(orderRequestDTO.getLotId());
        GeoRentUser rentee = findUserByPrincipal(principal);

        RentOrder orderToSave = mapFromRentOrderRequestDTO(orderRequestDTO, rentee, lot);

        RentOrder savedOrder = rentOrderRepository.save(orderToSave);

        GenericResponseDTO<RentOrderDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_ORDER.getDescription());
        responseDTO.setBody(mapToRentOrderDTO(savedOrder));
        return responseDTO;
    }


    private RentOrder mapFromRentOrderRequestDTO(
            final RentOrderRequestDTO requestDTO,
            final GeoRentUser rentee,
            final Lot lot) {

        RentOrder order = new RentOrder();
        order.setLot(lot);
        order.setRentee(rentee);
        order.setStartTime(requestDTO.getStartTime());
        order.setEndTime(requestDTO.getEndTime());
        order.setStatus(RentOrderStatus.PENDING);

        return order;
    }

    private RentOrderDTO mapToRentOrderDTO(final RentOrder rentOrder) {
        RentOrderDTO orderDTO = new RentOrderDTO();
        orderDTO.setOrderId(rentOrder.getOrderId());
        orderDTO.setLotDTO(mapToLotDto(rentOrder.getLot()));
        orderDTO.setRenteeId(rentOrder.getRentee().getId());
        orderDTO.setStartTime(rentOrder.getStartTime());
        orderDTO.setEndTime(rentOrder.getEndTime());
        orderDTO.setStatus(rentOrder.getStatus().toString());
        return orderDTO;
    }

    // TODO This method should be implemented as public in LotService or anywhere.
    // Until then we use this dummy version.
    private LotDTO mapToLotDto(Lot lot) {
        LotDTO lotDTO = new LotDTO();
        lotDTO.setId(lot.getId());
        return lotDTO;
    }

    private GeoRentUser findUserByPrincipal(Principal principal) {
        return userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                                Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
    }

    private GeoRentUser findUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        Message.INVALID_GET_USER_ID.getDescription() + userId));
    }

    private Lot findLotById(Long lotId) {
        return lotRepository
                .findById(lotId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        Message.INVALID_GET_LOT_ID.getDescription() + lotId));
    }
}
