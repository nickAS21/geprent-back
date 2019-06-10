package com.georent.dto;

import lombok.Data;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

@Data
public class DescriptionDTO {
    private List<Long> pictureIds = new ArrayList<Long>();
    private String itemName;
    private String lotDescription;
}
