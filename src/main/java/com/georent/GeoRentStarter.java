package com.georent;

import com.georent.domain.Coordinates;
import com.georent.domain.Description;
import com.georent.domain.GeoRentUser;
import com.georent.domain.Lot;
import com.georent.repository.GeoRentUserRepository;
import com.georent.repository.LotRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class GeoRentStarter {
    public static void main(String[] args) {
        SpringApplication.run(GeoRentStarter.class, args);
    }

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private GeoRentUserRepository userRepository;

    @Bean
    CommandLineRunner runner(){
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            GeoRentUser user = new GeoRentUser();
            user.setFirstName("Albert");
            user.setLastName("Bubbleschmidt");
            user.setEmail("email@email.com");
            user.setPhoneNumber("380951111111");
            user.setPassword(encoder.encode("MyPassw0rd"));

            GeoRentUser saved = userRepository.save(user);

            Set<Lot> collect = Stream.generate(() -> generateLot(saved))
                    .limit(5)
                    .collect(Collectors.toSet());
            lotRepository.saveAll(collect);
        };
    }

    private static Lot generateLot(GeoRentUser user) {
        Lot lot = new Lot();
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(RandomUtils.nextFloat());
        coordinates.setLongitude(RandomUtils.nextFloat());
        Description description = new Description();
        description.setItemName(RandomStringUtils.randomAlphabetic(10));
        description.setPictureId(RandomUtils.nextLong());
        description.setLotDescription(RandomStringUtils.randomAlphabetic(50));

        lot.setCoordinates(coordinates);
        lot.setDescription(description);
        lot.setGeoRentUser(user);
        return lot;
    }
}
