package com.finalproject.adephagia.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.finalproject.adephagia.util.LengthUnit.*;
import static com.finalproject.adephagia.util.SingularUnit.*;
import static com.finalproject.adephagia.util.TemperatureUnit.*;
import static com.finalproject.adephagia.util.VolumetricUnit.*;
import static com.finalproject.adephagia.util.WeightUnit.*;

public class ConversionValues {
    private final static Map<Unit, Function<Float, Float>> conversionMap = new HashMap<>() {{
        put(M, ConversionValues::meterToMeter);
        put(IN, ConversionValues::inchesToMeter);
        put(FT, ConversionValues::footToMeter);
        put(YRD, ConversionValues::yardToMeter);
        put(MI, ConversionValues::mileToMeter);
        put(SLICE, ConversionValues::sliceToPiece);
        put(PIECE, ConversionValues::pieceToPiece);
        put(F, ConversionValues::fahrenheitToCelsius);
        put(C, ConversionValues::celsiusToCelsius);
        put(K, ConversionValues::kelvinToCelsius);
        put(L, ConversionValues::literToLiter);
        put(FL_OZ, ConversionValues::fluidOunceToLiter);
        put(PINT, ConversionValues::pintToLiter);
        put(QUART, ConversionValues::quartToLiter);
        put(GAL, ConversionValues::gallonToLiter);
        put(CUP, ConversionValues::cupToLiter);
        put(TBSP, ConversionValues::tablespoonToLiter);
        put(TSP, ConversionValues::teaspoonToLiter);
        put(PINCH, ConversionValues::pinchToLiter);
        put(DASH, ConversionValues::dashToLiter);
        put(DUSTING, ConversionValues::dustingToLiter);
        put(G, ConversionValues::gramToGram);
        put(OZ, ConversionValues::ounceToGram);
        put(LB, ConversionValues::poundToGram);
        put(ST, ConversionValues::stoneToGram);
        put(T, ConversionValues::tonToGram);
    }};

    public static Float meterToMeter(Float quantity) {
        return quantity;
    }

    public static Float inchesToMeter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(39.37), RoundingMode.CEILING).floatValue();
    }

    public static Float footToMeter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(3.281), RoundingMode.CEILING).floatValue();
    }

    public static Float yardToMeter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(1.094), RoundingMode.CEILING).floatValue();
    }

    public static Float mileToMeter(Float quantity) {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(1609)).floatValue();
    }

    public static Float sliceToPiece(Float quantity) {
        return quantity;
    }

    public static Float pieceToPiece(Float quantity) {
        return quantity;
    }

    public static Float fahrenheitToCelsius(Float quantity) {
        // TODO: Test this to make sure it's right
        BigDecimal decimalQuantity = BigDecimal.valueOf(quantity);
        BigDecimal ratio = BigDecimal.valueOf(5).divide(BigDecimal.valueOf(9), RoundingMode.CEILING);
        return (decimalQuantity.subtract(BigDecimal.valueOf(32))).multiply(ratio).floatValue();
    }

    public static Float celsiusToCelsius(Float quantity) {
        return quantity;
    }

    public static Float kelvinToCelsius(Float quantity) {
        return BigDecimal.valueOf(quantity).subtract(BigDecimal.valueOf(-273.15)).floatValue();
    }

    public static Float literToLiter(Float quantity) {
        return quantity;
    }

    public static Float fluidOunceToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(33.814), RoundingMode.CEILING).floatValue();
    }

    public static Float pintToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(2.113), RoundingMode.CEILING).floatValue();
    }

    public static Float quartToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(1.057), RoundingMode.CEILING).floatValue();
    }

    public static Float gallonToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(3.785)).floatValue();
    }

    public static Float cupToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(4.227), RoundingMode.CEILING).floatValue();
    }

    public static Float tablespoonToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(67.628), RoundingMode.CEILING).floatValue();
    }

    public static Float teaspoonToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(202.9), RoundingMode.CEILING).floatValue();
    }

    public static Float pinchToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(32258.0645), RoundingMode.CEILING).floatValue();
    }

    public static Float dashToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(1612.9), RoundingMode.CEILING).floatValue();
    }

    public static Float dustingToLiter(Float quantity) {
        return BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(1612.9), RoundingMode.CEILING).floatValue();
    }

    public static Float gramToGram(Float quantity) {
        return quantity;
    }

    public static Float ounceToGram(Float quantity) {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(28.35)).floatValue();
    }

    public static Float poundToGram(Float quantity) {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(453.6)).floatValue();
    }

    public static Float stoneToGram(Float quantity) {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(6350.29)).floatValue();
    }

    public static Float tonToGram(Float quantity) {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(907200)).floatValue();
    }

}
