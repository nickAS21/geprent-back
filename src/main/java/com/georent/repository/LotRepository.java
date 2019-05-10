package com.georent.repository;

import com.georent.domain.Lot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long> {
    Optional<Lot> findByIdAndUserId(Long id, Long userId);
}
