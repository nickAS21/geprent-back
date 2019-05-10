package com.georent.repository;

import com.georent.domain.Description;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DescriptionRepository extends JpaRepository<Description, Long> {

    Optional<Description> findByIdAndUserId(Long id, Long userId);
}
