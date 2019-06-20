package com.georent.repository;

import com.georent.domain.RentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentOrderRepository extends JpaRepository<RentOrder, Long> {
    List<RentOrder> findAllByRenteeId(Long renteeId);
    List<RentOrder> findAllByLot_Id(Long lotId);
}
