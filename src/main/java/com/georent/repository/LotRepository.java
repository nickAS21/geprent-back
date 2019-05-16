package com.georent.repository;

import com.georent.domain.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {

    List<Lot> findAllByGeoRentUser_Id(Long userId);
    Lot findByIdAndGeoRentUser_Id(Long id, Long userId);
    void deleteLotsByIdAndGeoRentUser_Id(Long id, Long userId);
    void deleteAllByGeoRentUser_Id(Long userId);
}
