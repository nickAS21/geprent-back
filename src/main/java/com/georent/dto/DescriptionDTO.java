package com.georent.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class DescriptionDTO {
    private Long pictureId;
    private String itemName;
    private String lotDescription;
}
