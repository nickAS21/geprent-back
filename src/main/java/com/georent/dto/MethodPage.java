package com.georent.dto;

import lombok.Getter;


@Getter
public enum MethodPage {

    FIRST("first"),
    NEXT("next"),
    PREVIOUS("previous"),
    PREVIOUS_OR_FIRST("previousOrFirst"),
    LAST("last"),
    CURRENT("current");

    private String typeValue;

    private MethodPage(String type) {
        typeValue = type;
    }

    static public MethodPage getType(String pType) {
        for (MethodPage type: MethodPage.values()) {
            if (type.getTypeValue().equals(pType)) {
                return type;
            }
        }
        return MethodPage.valueOf("CURRENT");
    }

    public String getTypeValue() {
        return typeValue;
    }
}
