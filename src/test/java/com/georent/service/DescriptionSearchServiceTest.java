package com.georent.service;

import com.georent.dto.DescriptionDTO;
import com.georent.dto.LotPageDTO;
import com.georent.dto.LotPageable;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DescriptionSearchServiceTest {

    private final int pageNumber = 1;
    private final int count = 3;
    private final String methodPage = "next";
    private final String searchTerm = "Kiev";
    private final String lotName = "lotName2";

    @Autowired
    DescriptionSearchService descriptionSearchService;

    @Test
    void descriptionSearchServiceSuccessfullyStarted() {
        assertThat(descriptionSearchService != null);
    }

    @Test
    void whenFuzzyLotSearchSuccessfully_Return_ListDescriptionDTO() {
        List<DescriptionDTO> actualLotDTOS = descriptionSearchService.fuzzyLotSearch(searchTerm);
        assertThat(actualLotDTOS != null);
    }

    @Test
    void whenFuzzyLotNameAndAddressSearchSuccessfully_Return_SetLotPageDTO() {
        Set<LotPageDTO> actualPageDTO = descriptionSearchService.fuzzyLotNameAndAddressSearch(searchTerm, lotName);
        assertThat(actualPageDTO != null);
    }

    @Test
    void whenfuzzyLotPageNameAndAddressSearchSuccessfully_Return_LotPageable() {
        LotPageable actualLotPageable = descriptionSearchService.fuzzyLotPageNameAndAddressSearch(
                pageNumber,
                count,
                methodPage,
                searchTerm,
                lotName);
        assertThat(actualLotPageable != null);
        Assert.assertEquals(0, actualLotPageable.getTotalPages());
        Assert.assertEquals(1, actualLotPageable.getPageNumber());
    }
}



