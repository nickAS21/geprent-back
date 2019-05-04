package com.georent.repository;

import com.georent.domain.GeoRentUser;
import com.georent.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoRentUserRepository extends JpaRepository<GeoRentUser, Long> {

    Optional<GeoRentUser> findByEmail(String email);

    Boolean existsByEmail(String email);

}
