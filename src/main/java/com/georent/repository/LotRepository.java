package com.georent.repository;

import com.georent.domain.Lot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LotRepository extends JpaRepository<Lot, Long> {
}
