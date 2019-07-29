package com.georent.controller;

import com.georent.service.DescriptionSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("search")
@Slf4j
public class SearchController {


    private final DescriptionSearchService searchService;

    @Autowired
    public SearchController(DescriptionSearchService searchService) {
        this.searchService = searchService;
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
     * @param methodPage            return null;
     * @param address
     * @param lotName
     * @return Response, containing the list of all lots with filters: "address" and "lotname"
     * one page in the format  of List<LotPageDTO>  with pageNumber (LotPageable).
     */
    @GetMapping ("/page/{number}/{count}/{method}")
    public ResponseEntity<?> searchByAddressAndLotName(@PathVariable(value = "number") int numberPage,
                                                       @PathVariable(value = "count") int count,
                                                       @PathVariable(value = "method") String methodPage,
                                                       @RequestParam(name = "address") String address,
                                                       @RequestParam(name = "lotname") String lotName) {
        return status(OK)
                .body(searchService.fuzzyLotPageNameAndAddressSearch(numberPage-1, count, methodPage, address, lotName, false));
    }
    /**
     *
     * @param numberPage
     * @param count
     * @param methodPage            return null;
     * @param address
     * @param lotName
     * @return Response, containing the list of all lots with filters: "address" and "lotname"
     * one page in the format  of List<LotPageDTO>  with pageNumber (LotPageable).
     */
    @GetMapping ("/page/andor/{number}/{count}/{method}")
    public ResponseEntity<?> searchByAddressAndLotNameAndOr(@PathVariable(value = "number") int numberPage,
                                                       @PathVariable(value = "count") int count,
                                                       @PathVariable(value = "method") String methodPage,
                                                       @RequestParam(name = "address") String address,
                                                       @RequestParam(name = "lotname") String lotName,
                                                       @RequestParam(name = "andor") boolean andOr) {
        return status(OK)
                .body(searchService.fuzzyLotPageNameAndAddressSearch(numberPage-1, count, methodPage, address, lotName, andOr));
    }

}
