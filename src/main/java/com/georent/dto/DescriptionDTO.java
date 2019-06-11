package com.georent.dto;

import lombok.Data;

import javax.persistence.Column;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Data
public class DescriptionDTO {
    private List<Long> pictureIds = new ArrayList<Long>();
    private List<URL> URLs = new ArrayList<URL>();
    private String lotName;
    private String lotDescription;
}
