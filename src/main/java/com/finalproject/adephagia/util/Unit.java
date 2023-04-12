package com.finalproject.adephagia.util;

public interface Unit {
    enum UNIT_TYPE {
        VOLUMETRIC,
        LENGTH,
        MASS,
        TEMPERATURE,
        SINGULAR,
        NOT_LISTED
    }
    Unit getUnitEnum(String value);
    UNIT_TYPE getUnitType();
}
