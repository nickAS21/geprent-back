package com.georent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
public enum MetodPage {

    FIRST("first"),
    NEXT("next"),
    PREVOUS("previous"),
    PREVOUS_OR_FIRST("previousOrFirst"),
    LAST("last"),
    CURRENT("current");

    private String typeValue;

    private MetodPage(String type) {
        typeValue = type;
    }

    static public MetodPage getType(String pType) {
        for (MetodPage type: MetodPage.values()) {
            if (type.getTypeValue().equals(pType)) {
                return type;
            }
        }
        return MetodPage.valueOf("CURRENT");
    }

    public String getTypeValue() {
        return typeValue;
    }
}
