package com.georent.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class LotPageable {

    List<LotPageDTO> lots;
    int pageNumber;

    public LotPageable(List<LotPageDTO> lots, int pageNumber) {
        this.lots = lots;
        this.pageNumber = pageNumber;
    }
}
