package com.finalproject.adephagia.util;

import com.finalproject.adephagia.dto.Measurements;

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
    private final static Map<Unit, Function<Float, Measurements>> conversionMap = new HashMap<>() {{
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

    public static Function<Float, Measurements> getConversion(Unit unit) {
        try {
            return conversionMap.get(unit);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Measurements meterToMeter(Float quantity) {
        return new MeasurementsBuilder().units("m").quantity(quantity).build();
    }

    public static Measurements inchesToMeter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(39.37), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("m").quantity(returnQuantity).build();
    }

    public static Measurements footToMeter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(3.281), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("m").quantity(returnQuantity).build();
    }

    public static Measurements yardToMeter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(1.094), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("m").quantity(returnQuantity).build();
    }

    public static Measurements mileToMeter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(1609)).floatValue();
        return new MeasurementsBuilder().units("m").quantity(returnQuantity).build();
    }

    public static Measurements sliceToPiece(Float quantity) {
        return new MeasurementsBuilder().units("piece").quantity(quantity).build();
    }

    public static Measurements pieceToPiece(Float quantity) {
        return new MeasurementsBuilder().units("piece").quantity(quantity).build();
    }

    public static Measurements fahrenheitToCelsius(Float quantity) {
        // TODO: Test this to make sure it's right
        BigDecimal decimalQuantity = BigDecimal.valueOf(quantity);
        BigDecimal ratio = BigDecimal.valueOf(5).divide(BigDecimal.valueOf(9), RoundingMode.CEILING);
        Float returnQuantity = (decimalQuantity.subtract(BigDecimal.valueOf(32))).multiply(ratio).floatValue();
        return new MeasurementsBuilder().units("C").quantity(returnQuantity).build();
    }

    public static Measurements celsiusToCelsius(Float quantity) {
        return new MeasurementsBuilder().units("C").quantity(quantity).build();
    }

    public static Measurements kelvinToCelsius(Float quantity) {
        Float returnQuantity = BigDecimal.valueOf(quantity).subtract(BigDecimal.valueOf(-273.15)).floatValue();
        return new MeasurementsBuilder().units("C").quantity(returnQuantity).build();
    }

    public static Measurements literToLiter(Float quantity) {
        return new MeasurementsBuilder().units("L").quantity(quantity).build();
    }

    public static Measurements fluidOunceToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(33.814), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements pintToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(2.113), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements quartToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(1.057), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements gallonToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(3.785)).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements cupToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(4.227), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements tablespoonToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(67.628), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements teaspoonToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(202.9), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements pinchToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(32258.0645), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements dashToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(1612.9), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements dustingToLiter(Float quantity) {
        Float returnQuantity =
                BigDecimal.valueOf(quantity).divide(BigDecimal.valueOf(1612.9), RoundingMode.CEILING).floatValue();
        return new MeasurementsBuilder().units("L").quantity(returnQuantity).build();
    }

    public static Measurements gramToGram(Float quantity) {
        return new MeasurementsBuilder().units("g").quantity(quantity).build();
    }

    public static Measurements ounceToGram(Float quantity) {
        Float returnQuantity = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(28.35)).floatValue();
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

    public static Measurements poundToGram(Float quantity) {
        Float returnQuantity = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(453.6)).floatValue();
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

    public static Measurements stoneToGram(Float quantity) {
        Float returnQuantity = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(6350.29)).floatValue();
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

    public static Measurements tonToGram(Float quantity) {
        Float returnQuantity = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(907200)).floatValue();
        return new MeasurementsBuilder().units("g").quantity(returnQuantity).build();
    }

}
