package com.georent.service;

import com.georent.domain.GeoRentUser;
import com.georent.repository.GeoRentUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class GeoRentUserService {

    private final transient GeoRentUserRepository userRepository;
    private final transient PasswordEncoder passwordEncoder;

    @Autowired
    public GeoRentUserService(final GeoRentUserRepository userRepository,
                              final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<GeoRentUser> getUserByEmail (final String email) {
        return userRepository.findByEmail(email);
    }

    public Boolean existsUserByEmail (final String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public GeoRentUser saveNewUser (final GeoRentUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
