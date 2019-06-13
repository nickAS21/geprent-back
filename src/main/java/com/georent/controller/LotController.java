package com.georent.controller;


import com.georent.service.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

/**
 * Request controllers to the lot endpoints, that do not require authentication.
 */
@Controller
@RequestMapping("lot")
public class LotController {

    private final LotService lotService;

    @Autowired
    public LotController(final LotService lotService) {
        this.lotService = lotService;
    }

    /**
     * Processes the GET request to "/lot" URI.
     * @return Response, containing the list of coordinates of all lots, stored in the database.
     */
    @GetMapping
    public ResponseEntity<?> getLots(){
        return ResponseEntity.ok(lotService.getLotsDto());
    }

    /**
     * Processes the GET request to "/lot/{id}" URI.
     * @param lotId the id of the lot, specified in the request path.
     * @return Response, containing the requested lot in the format of LotDTO.
     */
   @GetMapping ("/{id}")
    public ResponseEntity<?> getLotId(@PathVariable(value = "id") Long lotId) {
        return status(OK).body(lotService.getLotDto(lotId));
    }


    /**
     * Processes the GET request to "/lot/page/{number}/{count}" URI.
     * @param numberPage
     * @param count
     * @return Response, containing the list of all lots one page in the format lotDto.
     */
    @GetMapping ("/page/{number}/{count}/{metod}")
    public ResponseEntity<?> getPage(@PathVariable(value = "number") int numberPage,
                                     @PathVariable(value = "count") int count,
                                     @PathVariable(value = "metod") String metodPage) {
        return status(OK).body(lotService.getPage(numberPage, count, metodPage));
    }
}
