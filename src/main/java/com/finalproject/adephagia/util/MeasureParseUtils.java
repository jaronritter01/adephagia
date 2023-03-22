package com.finalproject.adephagia.util;

import com.finalproject.adephagia.dto.Measurements;

import java.util.List;
import java.util.Map;

public class MeasureParseUtils {
    private static final List<String> COMMON_UNITS = List.of(
            "g", "oz", "tsp", "tbsp", "cup", "part", "ml", "fl oz"
    );
    private static final List<String> SINGULAR_UNITS = List.of(
            "slice", "pinch", "dash"
    );
    private static final Map<String, Float> SPECIAL_NUMBERS = Map.of(
            "½", 0.5F,
            "¼", 0.25F,
            "¾", 0.75F,
            "⅓", 0.33333F,
            "⅔", 0.66666F,
            "⅛", 0.125F,
            "⅜", 0.375F,
            "⅕", 0.2F
    );

    public static Measurements parseMeasurement(String measurement) {
        Float quantity = 0.0F;
        String unit = "";
        String[] unitArray = new String[2];
        measurement = measurement.trim().toLowerCase();
        // case that two measurements are provided
        if (hasDigit(measurement)) {
            if (hasSlash(measurement)) {
                unitArray = measurement.split("/");
            } else {
                unitArray[0] = measurement;
            }
            for (String posUnit : COMMON_UNITS){
                if (unitArray[0].contains(posUnit)){
                    int foundIndex = unitArray[0].indexOf(posUnit);
                    quantity = Float.valueOf(unitArray[0].substring(0, foundIndex));
                    unit = unitArray[0].substring(foundIndex);
                    break;
                }
            }
            // Case that it contains a special ascii fraction
        } else if (isAFrac(SPECIAL_NUMBERS, measurement)) {
            for (String posFrac : SPECIAL_NUMBERS.keySet()) {
                if (measurement.contains(posFrac)) {
                    quantity = SPECIAL_NUMBERS.get(posFrac);
                    break;
                }
            }
            for (String posUnit : COMMON_UNITS) {
                if (measurement.contains(posUnit)) {
                    int foundIndex = measurement.indexOf(posUnit);
                    unit = measurement.substring(foundIndex);
                    break;
                }
            }
        }
        // Case for pinch, slice, dash and not pinches, slices, or dashes,
        else if (isASingularUnit(measurement)){
            quantity = 1.0F;
            unit = measurement;
        }
        // Normal Space Unit Case
        else {
            unitArray = measurement.split(" ");
            quantity = Float.valueOf(unitArray[0]);
            unit = unitArray[1];
        }

        System.out.println(String.format("Quantity: %.2f, Unit: %s", quantity, unit));

        return new MeasurementsBuilder().units(unit).quantity(quantity).build();
    }

    public static boolean isASingularUnit(String strToCheck){
        for (String str : SINGULAR_UNITS) {
            if (strToCheck.contains(str)){
                return true;
            }
        }

        return false;
    }

    public static boolean isAFrac(Map<String, Float> fracMap, String strToCheck){
        for (String frac : fracMap.keySet()) {
            if (strToCheck.contains(frac)){
                return true;
            }
        }

        return false;
    }

    public static boolean hasDigit(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSlash(String str) {
        for (char c : str.toCharArray()) {
            if (c == '/') {
                return true;
            }
        }
        return false;
    }
}
