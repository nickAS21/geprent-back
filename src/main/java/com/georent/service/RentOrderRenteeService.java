package com.georent.service;

import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.domain.RentOrder;
import com.georent.domain.RentOrderStatus;
import com.georent.dto.GenericResponseDTO;
import com.georent.dto.LotDTO;
import com.georent.dto.RentOrderDTO;
import com.georent.dto.RentOrderRequestDTO;
import com.georent.exception.BasicExceptionHandler;
import com.georent.exception.LotNotFoundException;
import com.georent.exception.OrderNotFoundException;
import com.georent.exception.OrderOverlapsApprovedOrdersException;
import com.georent.exception.RentOrderUpdateException;
import com.georent.message.Message;
import com.georent.repository.GeoRentUserRepository;
import com.georent.repository.LotRepository;
import com.georent.repository.RentOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentOrderRenteeService {
    private final GeoRentUserService userService;
    private final RentOrderRepository rentOrderRepository;
    private final GeoRentUserRepository userRepository;
    private final LotRepository lotRepository;

    @Autowired
    public RentOrderRenteeService(GeoRentUserService userService,
                                  RentOrderRepository rentOrderRepository,
                                  GeoRentUserRepository userRepository,
                                  LotRepository lotRepository) {
        this.userService = userService;
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

        if (orderOverlapsAtLeastOneApprovedOrderFromTheLot(orderToSave)) {
            throw new OrderOverlapsApprovedOrdersException(
                    Message.INVALID_SAVE_ORDER.getDescription()
                            + Message.ORDER_OVERLAPS.getDescription());
        }

        RentOrder savedOrder = rentOrderRepository.save(orderToSave);

        GenericResponseDTO<RentOrderDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_SAVE_ORDER.getDescription());
        responseDTO.setBody(mapToRentOrderDTO(savedOrder));

        return responseDTO;
    }

    /**
     * Reads the list of user (rentee) orders from the database
     * and maps them to the RentOrderDTO format.
     * @param principal Current user (rentee) identifier.
     * @return The list of user (rentee) orders in the RentOrderDTO format.
     */
    public List<RentOrderDTO> getRenteeOrders(Principal principal) {
        GeoRentUser rentee = findUserByPrincipal(principal);
        List<RentOrder> renteeOrders = findOrdersByRentee(rentee);
        return renteeOrders
                .stream()
                .map(this::mapToRentOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * Reads the list of user (rentee) orders to the specified lot from the database
     * and maps them to the RentOrderDTO format.
     * @param principal Current user (rentee) identifier.
     * @param lotId lot identifier.
     * @return The list of user (rentee) orders in the RentOrderDTO format.
     */
    public List<RentOrderDTO> getRenteeOrdersToLot(Long lotId, Principal principal) {
        GeoRentUser rentee = findUserByPrincipal(principal);
        Lot lot = findLotById(lotId);

        List<RentOrder> renteeOrdersToLot = findOrdersByLotAndRentee(lot, rentee);

        return renteeOrdersToLot
                .stream()
                .map(this::mapToRentOrderDTO)
                .collect(Collectors.toList());
    }

    /**
     * Reads the order with provided id from the database,
     * and maps it to the RentOrderDTO format.
     * Checks if this user has the access to this order, if not,
     * throws OrderNotFoundException.
     * @param orderId the id of the order.
     * @param principal current user (rentee) authentifier.
     * @return the order in the RentOrderDTO format.
     */
    public RentOrderDTO getRenteeOrderById(Long orderId, Principal principal) {
        GeoRentUser rentee = findUserByPrincipal(principal);

        RentOrder order = findByIdAndRentee(orderId, rentee);

        return mapToRentOrderDTO(order);
    }

    /**
     * Updates start and end dates in the order with provided id.
     * Checks if this user has the access to this order.
     * If not, throws OrderNotFoundException.
     * Checks if this order status is pending.
     * If not, throws RentOrderUpdateException.
     * @param orderId the id of the order to update.
     * @param updateOrderDTO - the new values.
     * @param principal - user (rentee) identifier.
     * @return
     */
    public GenericResponseDTO<RentOrderDTO> patchRenteeOrderById(
            Long orderId, RentOrderDTO updateOrderDTO, Principal principal) {

        GeoRentUser rentee = findUserByPrincipal(principal);

        RentOrder orderToUpdate = findByIdAndRentee(orderId, rentee);

        if (!RentOrderStatus.PENDING.equals(orderToUpdate.getStatus())) {
            throw new RentOrderUpdateException(Message.INVALID_UPDATE_ORDER.getDescription());
        }

        orderToUpdate.setStartTime(updateOrderDTO.getStartTime());
        orderToUpdate.setEndTime(updateOrderDTO.getEndTime());

        if (orderOverlapsAtLeastOneApprovedOrderFromTheLot(orderToUpdate)) {
            throw new OrderOverlapsApprovedOrdersException(
                    Message.INVALID_SAVE_ORDER.getDescription()
                            + Message.ORDER_OVERLAPS.getDescription());
        }

        rentOrderRepository.save(orderToUpdate);

        GenericResponseDTO<RentOrderDTO> responseDTO = new GenericResponseDTO<>();
        responseDTO.setMessage(Message.SUCCESS_UPDATE_ORDER.getDescription());
        responseDTO.setBody(mapToRentOrderDTO(orderToUpdate));

        return responseDTO;
    }

    public List<RentOrder> findOrdersByRentee(GeoRentUser rentee) {
        return rentOrderRepository.findByRentee_Id(rentee.getId());
    }

    public List<RentOrder> findOrdersByLotAndRentee(Lot lot, GeoRentUser rentee) {
        return rentOrderRepository.findByLot_IdAndRentee_Id(lot.getId(), rentee.getId());
    }


    /**
     * Deletes the order with provided id from the database.
     * Checks if this user has the access to this order.
     * If not, throws OrderNotFoundException.
     * @param orderId the id of the order to delete.
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    public GenericResponseDTO<RentOrderDTO> deleteRenteeOrderById(Long orderId, Principal principal) {
        GeoRentUser rentee = findUserByPrincipal(principal);

        RentOrder orderToDelete = findByIdAndRentee(orderId, rentee);

        rentOrderRepository.delete(orderToDelete);

        GenericResponseDTO<RentOrderDTO> response = new GenericResponseDTO<>();
        response.setMessage(Message.SUCCESS_DELETE_ORDER.getDescription());
        return  response;
    }

    /**
     * Deletes all the orders of this user.
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    public GenericResponseDTO<RentOrderDTO> deleteRenteeOrders(Principal principal) {
        GeoRentUser rentee = findUserByPrincipal(principal);

        rentOrderRepository.deleteAllByRentee_Id(rentee.getId());

        GenericResponseDTO<RentOrderDTO> response = new GenericResponseDTO<>();
        response.setMessage(Message.SUCCESS_DELETE_ORDERS.getDescription());
        return response;
    }

    /**
     * Deletes all the orders of this user to the lot with provided id.
     * @param lotId the id of the lot, from which to delete the orders
     * @param principal current user (rentee) identifier.
     * @return the response with delete successful message.
     */
    public GenericResponseDTO<RentOrderDTO> deleteRenteeOrdersToLot(
            Long lotId, Principal principal) {

        GeoRentUser rentee = findUserByPrincipal(principal);

        rentOrderRepository.deleteAllByLot_IdAndRentee_Id(lotId, rentee.getId());

        GenericResponseDTO<RentOrderDTO> response = new GenericResponseDTO<>();
        response.setMessage(Message.SUCCESS_DELETE_ORDERS.getDescription());
        return response;
    }


    // TODO This method should be implemented as public in LotService or anywhere.
    // Until then we use this dummy version.
    private LotDTO mapToLotDto(Lot lot) {
        LotDTO lotDTO = new LotDTO();
        lotDTO.setId(lot.getId());
        return lotDTO;
    }

    // TODO This method should be implemented as public in GeoRentUserService or anywhere.
    public GeoRentUser findUserByPrincipal(Principal principal) {
        return userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        Message.INVALID_GET_USER_EMAIL.getDescription() + principal.getName()));
    }

    // TODO This method should be implemented as public in GeoRentUserService or anywhere.
    public GeoRentUser findUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        Message.INVALID_GET_USER_ID.getDescription() + userId));
    }

    // TODO This method should be implemented as public in LotService or anywhere.
    public Lot findLotById(Long lotId) {
        return lotRepository
                .findById(lotId)
                .orElseThrow(() -> new LotNotFoundException(
                        Message.INVALID_GET_LOT_ID.getDescription() + lotId));
    }

    // TODO This method should be implemented as public in LotService or anywhere.
    public boolean lotIsOwnedByUser(final Lot lot, final GeoRentUser user) {
        return user.getId() == lot.getGeoRentUser().getId();
    }

    private RentOrder findByIdAndRentee(Long orderId, GeoRentUser rentee) {
        return rentOrderRepository
                .findByOrderIdAndRentee(orderId, rentee)
                .orElseThrow(() -> new OrderNotFoundException(
                        Message.INVALID_GET_ORDER.getDescription() + orderId)
                );
    }

    /**
     * Checks if this order was submitted by this user.
     * @param order the order to check
     * @param user the user to check
     * @return true, if the order was submitted by the user, false otherwise.
     */
    public boolean orderWasSubmittedByUser(final RentOrder order, final GeoRentUser user) {
        return order.getRentee().getId() == user.getId();
    }

    /**
     * Check if the time intervals in this two orders overlaps.
     * @param orderA Order to check against another.
     * @param orderB Order to check against another.
     * @return true orders overlaps, false otherwise.
     */
    public boolean ordersOverlaps(final RentOrder orderA, final RentOrder orderB) {
        return orderA.getStartTime().isBefore(orderB.getEndTime())
                && orderB.getStartTime().isBefore(orderA.getEndTime());
    }

    /**
     * Check if the time interval in this order overlaps with the time interval
     * in at least one of the orders from this order's lot.
     * @param order the order to check
     * @return true if overlaps, false otherwise.
     */
    public boolean orderOverlapsAtLeastOneApprovedOrderFromTheLot(final RentOrder order) {
        return rentOrderRepository
                .findByLotAndStatus(order.getLot(), RentOrderStatus.APPROVED)
                .stream()
                .anyMatch( approvedOrderFromLot -> ordersOverlaps(order, approvedOrderFromLot));
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


}
