package com.georent.controller;

import com.georent.domain.Description;
import com.georent.dto.DescriptionDTO;
import com.georent.service.DescriptionSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("search")
public class SearchController {


    private final DescriptionSearchService searchService;

    @Autowired
    public SearchController(DescriptionSearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Search all lots with filters: "query" on Fields: "lotName" and "lotDescription" in class Description
     * @param query
     * @return Response, containing the all lots with filters: "query" on Fields: "lotName" and "lotDescription" in the format  List<DescriptionDTO>
     */
    @GetMapping
    public ResponseEntity<List<DescriptionDTO>> findDescriptions(@RequestParam(name = "query") String query){
        return ResponseEntity.ok(searchService.fuzzyLotSearch(query));
    }

    /**
     * Search allLots with filters: "address" to the field address in class Coordinates
     * Search allLots with filters: "lotname" to the field lotName in class Description
     * @param address
     * @param lotName
     * @return Response, containing the all lots with filters: "address" and "lotname" in the format  Set<LotPageDTO>
     */
    @GetMapping ("/filters")
    public ResponseEntity<?> findLotsAdrName(@RequestParam(name = "address") String address,
                                             @RequestParam(name = "lotname") String lotName
    ){
        return status(OK).body(searchService.fuzzyLotNameAndAddressSearch(address, lotName));
    }


    /**
     *
     * @param numberPage
     * @param count
     * @param metodPage
     * @param address
     * @param lotName
     * @return Response, containing the list of all lots with filters: "address" and "lotname"
     * one page in the format  of List<LotPageDTO>  with pageNumber (LotPageable).
     */
    @GetMapping ("/page/{number}/{count}/{metod}")
    public ResponseEntity<?> getPage(@PathVariable(value = "number") int numberPage,
                                     @PathVariable(value = "count") int count,
                                     @PathVariable(value = "metod") String metodPage,
                                     @RequestParam(name = "address") String address,
                                     @RequestParam(name = "lotname") String lotName
    ) {
        return status(OK).body(searchService.fuzzyLotPageNameAndAddressSearch(numberPage-1, count, metodPage, address, lotName));
    }

}
