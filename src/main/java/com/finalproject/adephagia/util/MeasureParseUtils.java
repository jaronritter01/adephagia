package com.finalproject.adephagia.util;

import com.finalproject.adephagia.dto.Measurements;

import java.util.List;
import java.util.Map;

public class MeasureParseUtils {
    private static final List<String> COMMON_UNITS = List.of(
            "g", "oz", "tsp", "tbsp", "cup", "part", "ml", "fl oz"
    );
    private static final List<String> SINGULAR_UNITS = List.of(
            "slice", "pinch", "dash", "dusting", "piece"
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

    public static void main (String[] args){
        // This is currently working up to "1kg"
        List<String> testList = List.of(
                "175g/6oz", "2 medium", "½ tsp", "Pinch", "1 tbsp", "2-3 ml", "1", "8 thin slices", "Dusting",
                "50ml/2fl oz", "1-2tbsp", "750g piece", "1kg", "1.5kg", "1/2 cup", "1/2 lb", "1/4 lb", "Juice of 1",
                "Grated Zest of 2", "1/4", "1/8 teaspoon"
        );
        // need to be sure to trim the input, and toLower
//        for (int i = 0; i < 11; i++) {
//            String testString = testList.get(i);
//            parseMeasurement(testString);
//        }

        parseMeasurement(testList.get(11));
    }

    public static Measurements parseMeasurement(String measurement) {
        Float quantity = 0.0F;
        String unit = "";
        String[] unitArray = new String[2];
        measurement = measurement.trim().toLowerCase();
        // Case for pinch, slice, dash and not pinches, slices, or dashes. I.E. singular units
        if (isASingularUnit(measurement)){
            quantity = 1.0F;
            unit = measurement;
        }
        else if (hasDigit(measurement)) {
            // case that two measurements are provided
            if (hasSlash(measurement)) {
                unitArray = measurement.split("/");
            } else {
                unitArray[0] = measurement;
            }

            if (hasSpace(measurement) && !measurement.contains("fl oz")) {
                String[] measureArr = measurement.split(" ");
                if (hasDash(measureArr[0])) {
                    quantity = splitTheDash(measureArr[0]);
                } else {
                    quantity = Float.valueOf(measureArr[0]);
                }
                if(measureArr.length ==2){
                    unit = measureArr[1];
                } else{
                    unit = getUnitOfSizeTwoPlus(measureArr);
                }
            } else {
                boolean containsUnit = false;
                for (String posUnit : COMMON_UNITS){
                    if (unitArray[0].contains(posUnit)){
                        containsUnit = true;
                        int foundIndex = unitArray[0].indexOf(posUnit);
                        String strQuantity = unitArray[0].substring(0, foundIndex);
                        if (!hasSpace(measurement) && hasDash(measurement)) {
                            quantity = splitTheDash(strQuantity);
                        }
                        else {
                            quantity = Float.valueOf(strQuantity);
                        }
                        unit = unitArray[0].substring(foundIndex);
                        break;
                    }
                }

                if (!containsUnit) {
                    // assume whole thing is quantity
                    quantity = Float.valueOf(measurement);
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
        // Not sure what case this should be
        else {
            System.out.println("else");
        }

        System.out.printf("Quantity: %.2f, Unit: %s%n", quantity, unit);

        return new MeasurementsBuilder().units(unit).quantity(quantity).build();
    }

    public static Float splitTheDash(String measure){
        String[] quantityArr = measure.split("-");
        return Float.valueOf(quantityArr[0]);
    }

    public static String getUnitOfSizeTwoPlus(String[] arr){
        StringBuilder returnString = new StringBuilder();
        for (int i = 1; i < arr.length; i++){
            returnString.append(arr[i]).append(" ");
        }
        return returnString.toString().trim();
    }
    public static boolean hasDash(String str) {
        return str.contains("-");
    }

    public static boolean hasSpace(String str) {
        return str.contains(" ");
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
