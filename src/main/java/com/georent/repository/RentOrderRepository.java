package com.georent.repository;

import com.georent.domain.RentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentOrderRepository extends JpaRepository<RentOrder, Long> {
    List<RentOrder> findAllByRenteeId(Long renteeId);

    List<RentOrder> findAllByOwnerId(Long ownerId);
    Optional<RentOrder> findById(Long Id);
    List<RentOrder> findAllByLotId(Long lotId);
    List<RentOrder> deleteAllByOwnerId(Long ownerId);
    void deleteById(Long Id);
    List<RentOrder> deleteAllByLotId(Long lotId);
    RentOrder getRentOrderById(Long id);


}
