package com.georent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class GeoRentStarter {

    public static void main(String[] args) {
        SpringApplication.run(GeoRentStarter.class, args);
    }
//    @Autowired
//    private LotRepository lotRepository;
//
//    @Autowired
//    private GeoRentUserRepository userRepository;
//
//    @Bean
//    CommandLineRunner runner(){
//        return args -> {
//            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//            GeoRentUser user = new GeoRentUser();
//            user.setFirstName("Albert");
//            user.setLastName("Bubbleschmidt");
//            user.setEmail("email@email.com");
//            user.setPhoneNumber("380951111111");
//            user.setPassword(encoder.encode("MyPassw0rd"));
//
//            GeoRentUser saved = userRepository.save(user);
//
//            Set<Lot> collect = Stream.generate(() -> generateLot(saved))
//                    .limit(5)
//                    .collect(Collectors.toSet());
//            lotRepository.saveAll(collect);


//        };
//    }

//    private static Lot generateLot(GeoRentUser user) {
//        Lot lot = new Lot();
//        Coordinates coordinates = new Coordinates();
//        coordinates.setLatitude(RandomUtils.nextFloat());
//        coordinates.setLongitude(RandomUtils.nextFloat());
//        Description description = new Description();
//        description.setItemName(RandomStringUtils.randomAlphabetic(10));
//        description.setPictureId(RandomUtils.nextLong());
//        description.setLotDescription(RandomStringUtils.randomAlphabetic(50));
//
//        lot.setCoordinates(coordinates);
//        lot.setDescription(description);
//        lot.setGeoRentUser(user);
//        return lot;
//    }
}
