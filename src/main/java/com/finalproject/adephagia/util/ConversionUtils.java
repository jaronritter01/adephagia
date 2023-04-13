package com.finalproject.adephagia.util;

import com.finalproject.adephagia.dto.Measurements;

public class ConversionUtils {
    private static final VolumetricUnit volumetricUnit = VolumetricUnit.DEFAULT;
    private static final LengthUnit lengthUnit = LengthUnit.DEFAULT;
    private static final SingularUnit singularUnit = SingularUnit.DEFAULT;
    private static final TemperatureUnit temperatureUnit = TemperatureUnit.DEFAULT;
    private static final WeightUnit weightUnit = WeightUnit.DEFAULT;

    public static Measurements convertToStandardUnit(String unitToConvert, Float quantity) {
        Unit unit = getUnit(unitToConvert);
        if (unit == null) {
            return new MeasurementsBuilder().units("default").quantity(quantity).build();
        }

        return ConversionValues.getConversion(unit).apply(quantity);
    }

    private static Unit getUnit(String unitToConvert) {
        Unit volUnit = volumetricUnit.getUnitEnum(unitToConvert);
        Unit lenUnit = lengthUnit.getUnitEnum(unitToConvert);
        Unit singUnit = singularUnit.getUnitEnum(unitToConvert);
        Unit tempUnit = temperatureUnit.getUnitEnum(unitToConvert);
        Unit wgtUnit = weightUnit.getUnitEnum(unitToConvert);

        if (volUnit != null) {
            return volUnit;
        }

        if (lenUnit != null) {
            return lenUnit;
        }

        if (singUnit != null) {
            return singUnit;
        }

        if (tempUnit != null) {
            return tempUnit;
        }

        return wgtUnit;
    }
}
