package com.georent.repository;

import com.georent.domain.Lot;
import com.georent.domain.RentOrder;
import com.georent.domain.RentOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentOrderRepository extends JpaRepository<RentOrder, Long> {

    List<RentOrder> findByLot_Id(Long lotId);

    List<RentOrder> findByRentee_Id(Long renteeId);

    List<RentOrder> findByLot_IdAndRentee_Id(Long lotId, Long renteeId);

    List<RentOrder> findByLot_IdAndStatus(Long lotId, RentOrderStatus status);

    List<RentOrder> findByLotAndStatus(Lot lot, RentOrderStatus status);

    void deleteAllByLot_Id(Long lotId);

    void deleteAllByRentee_Id(Long renteeId);

    void deleteAllByLot_IdAndRentee_Id(Long lotId, Long renteeId);
}
