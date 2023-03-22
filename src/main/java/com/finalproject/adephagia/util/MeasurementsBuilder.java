package com.finalproject.adephagia.util;

import com.finalproject.adephagia.dto.Measurements;

public class MeasurementsBuilder {
    private Measurements measurements = new Measurements();

    public MeasurementsBuilder units(String units) {
        measurements.setUnit(units);
        return this;
    }

    public MeasurementsBuilder quantity(Float quantity){
        measurements.setQuantity(quantity);
        return this;
    }

    public Measurements build() {
        return measurements;
    }
}
