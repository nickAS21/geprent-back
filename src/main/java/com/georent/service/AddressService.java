package com.georent.service;

import com.georent.domain.Address;
import com.georent.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public Address getById(Long id) {
        Optional<Address> byId = addressRepository.findById(id);
        Address address = byId.orElseThrow(() -> new NoSuchElementException("No address with id: " + id));
        return address;
    }

    public void deleteById(Long id) {
        addressRepository.deleteById(id);
    }
}
