package com.georent.service;

import com.georent.domain.Lot;
import com.georent.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Optional;

public class LotService {

    private final transient LotRepository lotRepository;

    @Autowired
    public LotService(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    public Optional<Lot> getLotById (final Long id) {
        return lotRepository.findById(id);
    }

    public Boolean existsLotById (final Long id) {
        return lotRepository.existsById(id);
    }

    @Transactional
    public Lot saveNewLot (final Lot lot) {
        return lotRepository.save(lot);
    }
}
