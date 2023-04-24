package com.finalproject.adephagia.util;

import com.finalproject.adephagia.dao.RecipeItem;
import com.finalproject.adephagia.dto.Measurements;
import jakarta.transaction.Transactional;

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

    public static Unit.UNIT_TYPE getUnitType(String unitToCheck) {
        Unit unit = getUnit(unitToCheck);
        return unit == null ? Unit.UNIT_TYPE.NOT_LISTED : unit.getUnitType();
    }

    @Transactional
    public static void convertRecipeItem(RecipeItem recipeItem, String mostFreqUnit) {
        if (recipeItem.getMeasurementUnit().equals("default")) {
            return;
        }
        // This should really only apply for volume vs mass units
        // This is an estimate conversion based off the density of water
        switch (mostFreqUnit) {
            // Weight Unit
            case "g" -> {
                // Don't think you can convert grams to anything else
                recipeItem.setMeasurementUnit("g");
                Float conversionValue = recipeItem.getQuantity() * 1000;
                recipeItem.setQuantity(conversionValue);
            }
            // Volume Unit
            case "L" -> {
                recipeItem.setMeasurementUnit("L");
                Float conversionValue = recipeItem.getQuantity() / 1000;
                recipeItem.setQuantity(conversionValue);
            }
            // Note there should not be a default to anything or anything to default conversion
            // Do nothing
            default -> {}
        }
    }
}
