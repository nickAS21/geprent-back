package com.georent.repository;

import com.georent.domain.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {

    List<Lot> findAllByGeoRentUser_Id(Long userId);

    //    Lot findByIdAndUserId(Long userId, Long Lot.getId());
    @Query("SELECT v FROM Lot v JOIN FETCH v.geoRentUser user WHERE user.id=?1 and v.id=?2")
    Lot findLotByUser_Id(Long userId, Long id);

//    Lot findLotByUser_Id(Long userId, Long id);

}
