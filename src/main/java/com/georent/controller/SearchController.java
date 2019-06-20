package com.georent.controller;

import com.georent.domain.Description;
import com.georent.dto.DescriptionDTO;
import com.georent.service.DescriptionSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("search")
public class SearchController {


    private final DescriptionSearchService searchService;

    @Autowired
    public SearchController(DescriptionSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<List<DescriptionDTO>> findDescriptions(@RequestParam(name = "query") String query){
        List<Description> descriptions = searchService.fuzzyLotSearch(query);
        List<DescriptionDTO> dtos = descriptions
                .stream()
                .map(description -> {
                    DescriptionDTO dto = new DescriptionDTO();
                    dto.setLotDescription(description.getLotDescription());
                    dto.setLotName(description.getLotName());
                    dto.setPictureIds(description.getPictureIds());
                    dto.setURLs(Collections.emptyList());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

}
