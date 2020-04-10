package mekanism.common.util;

import javax.annotation.Nonnull;
import mekanism.api.IIncrementalEnum;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.IHasTranslationKey;
import mekanism.common.MekanismLang;
import mekanism.common.base.ILangEntry;
import mekanism.common.util.text.TextComponentUtil;
import net.minecraft.util.text.ITextComponent;

/**
 * Code taken from UE and modified to fit Mekanism.
 */
public class UnitDisplayUtils {//TODO: Maybe at some point improve on the ITextComponents the two getDisplay methods build, and have them have better translation keys with formats

    /**
     * Displays the unit as text. Does not handle negative numbers, as {@link FloatingLong} does not have a concept of negatives
     */
    public static ITextComponent getDisplay(FloatingLong value, ElectricUnit unit, int decimalPlaces, boolean isShort) {
        ILangEntry label = unit.pluralLangEntry;
        if (isShort) {
            label = unit.shortLangEntry;
        } else if (value.equals(FloatingLong.ONE)) {
            label = unit.singularLangEntry;
        }
        if (value.isZero()) {
            return TextComponentUtil.build(value + " ", label);
        }
        for (int i = 0; i < EnumUtils.FLOATING_LONG_MEASUREMENT_UNITS.length; i++) {
            FloatingLongMeasurementUnit lowerMeasure = EnumUtils.FLOATING_LONG_MEASUREMENT_UNITS[i];
            if (i == 0 && lowerMeasure.below(value)) {
                return TextComponentUtil.build(lowerMeasure.process(value).toString(decimalPlaces) + " " + lowerMeasure.getName(isShort), label);
            }
            if (i + 1 >= EnumUtils.FLOATING_LONG_MEASUREMENT_UNITS.length) {
                return TextComponentUtil.build(lowerMeasure.process(value).toString(decimalPlaces) + " " + lowerMeasure.getName(isShort), label);
            } else {
                FloatingLongMeasurementUnit upperMeasure = EnumUtils.FLOATING_LONG_MEASUREMENT_UNITS[i + 1];
                if ((lowerMeasure.above(value) && upperMeasure.below(value)) || lowerMeasure.value.equals(value)) {
                    return TextComponentUtil.build(lowerMeasure.process(value).toString(decimalPlaces) + " " + lowerMeasure.getName(isShort), label);
                }
            }
        }
        return TextComponentUtil.build(value.toString(decimalPlaces), label);
    }

    public static ITextComponent getDisplayShort(FloatingLong value, ElectricUnit unit) {
        return getDisplay(value, unit, 2, true);
    }

    public static ITextComponent getDisplay(double T, TemperatureUnit unit, int decimalPlaces, boolean shift, boolean isShort) {
        ILangEntry label = unit.langEntry;
        String prefix = "";
        double value = unit.convertFromK(T, shift);
        if (value < 0) {
            value = Math.abs(value);
            prefix = "-";
        }
        if (value == 0) {
            return isShort ? TextComponentUtil.getString(value + unit.symbol) : TextComponentUtil.build(value, label);
        }
        for (int i = 0; i < EnumUtils.MEASUREMENT_UNITS.length; i++) {
            MeasurementUnit lowerMeasure = EnumUtils.MEASUREMENT_UNITS[i];
            if (lowerMeasure.below(value) && lowerMeasure.ordinal() == 0) {
                if (isShort) {
                    return TextComponentUtil.getString(prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + lowerMeasure.symbol + unit.symbol);
                }
                return TextComponentUtil.build(prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + " " + lowerMeasure.name, label);
            }
            if (lowerMeasure.ordinal() + 1 >= EnumUtils.MEASUREMENT_UNITS.length) {
                if (isShort) {
                    return TextComponentUtil.getString(prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + lowerMeasure.symbol + unit.symbol);
                }
                return TextComponentUtil.build(prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + " " + lowerMeasure.name, label);
            }
            if (i + 1 < EnumUtils.MEASUREMENT_UNITS.length) {
                MeasurementUnit upperMeasure = EnumUtils.MEASUREMENT_UNITS[i + 1];
                if ((lowerMeasure.above(value) && upperMeasure.below(value)) || lowerMeasure.value == value) {
                    if (isShort) {
                        return TextComponentUtil.getString(prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + lowerMeasure.symbol + unit.symbol);
                    }
                    return TextComponentUtil.build(prefix + roundDecimals(lowerMeasure.process(value), decimalPlaces) + " " + lowerMeasure.name, label);
                }
            }
        }
        if (isShort) {
            return TextComponentUtil.getString(prefix + roundDecimals(value, decimalPlaces) + unit.symbol);
        }
        return TextComponentUtil.build(prefix + roundDecimals(value, decimalPlaces) + " ", label);
    }

    public static ITextComponent getDisplayShort(double value, TemperatureUnit unit) {
        return getDisplayShort(value, unit, true);
    }

    public static ITextComponent getDisplayShort(double value, TemperatureUnit unit, boolean shift) {
        return getDisplayShort(value, unit, shift, 2);
    }

    public static ITextComponent getDisplayShort(double value, TemperatureUnit unit, boolean shift, int decimalPlaces) {
        return getDisplay(value, unit, decimalPlaces, shift, true);
    }

    public static double roundDecimals(double d, int decimalPlaces) {
        int j = (int) (d * Math.pow(10, decimalPlaces));
        return j / Math.pow(10, decimalPlaces);
    }

    public static double roundDecimals(double d) {
        return roundDecimals(d, 2);
    }

    public enum ElectricUnit {
        JOULES(MekanismLang.ENERGY_JOULES, MekanismLang.ENERGY_JOULES_PLURAL, MekanismLang.ENERGY_JOULES_SHORT),
        FORGE_ENERGY(MekanismLang.ENERGY_FORGE, MekanismLang.ENERGY_FORGE, MekanismLang.ENERGY_FORGE_SHORT),
        ELECTRICAL_UNITS(MekanismLang.ENERGY_EU, MekanismLang.ENERGY_EU_PLURAL, MekanismLang.ENERGY_EU_SHORT);

        private final ILangEntry singularLangEntry;
        private final ILangEntry pluralLangEntry;
        private final ILangEntry shortLangEntry;

        ElectricUnit(ILangEntry singularLangEntry, ILangEntry pluralLangEntry, ILangEntry shortLangEntry) {
            this.singularLangEntry = singularLangEntry;
            this.pluralLangEntry = pluralLangEntry;
            this.shortLangEntry = shortLangEntry;
        }
    }

    public enum TemperatureUnit {
        KELVIN(MekanismLang.TEMPERATURE_KELVIN, "K", 0, 1),
        CELSIUS(MekanismLang.TEMPERATURE_CELSIUS, "°C", 273.15, 1),
        RANKINE(MekanismLang.TEMPERATURE_RANKINE, "R", 0, 1.8),
        FAHRENHEIT(MekanismLang.TEMPERATURE_FAHRENHEIT, "°F", 459.67, 1.8),
        AMBIENT(MekanismLang.TEMPERATURE_AMBIENT, "+STP", 300, 1);

        private final ILangEntry langEntry;
        //TODO: Do we want to make the symbol be localized?
        private final String symbol;
        public final double zeroOffset;
        public final double intervalSize;

        TemperatureUnit(ILangEntry langEntry, String symbol, double offset, double size) {
            this.langEntry = langEntry;
            this.symbol = symbol;
            this.zeroOffset = offset;
            this.intervalSize = size;
        }

        public double convertFromK(double T, boolean shift) {
            return (T * intervalSize) - (shift ? zeroOffset : 0);
        }

        public double convertToK(double T, boolean shift) {
            return (T + (shift ? zeroOffset : 0)) / intervalSize;
        }
    }

    /**
     * Metric system of measurement.
     */
    public enum MeasurementUnit {
        FEMTO("Femto", "f", 0.000000000000001D),
        PICO("Pico", "p", 0.000000000001D),
        NANO("Nano", "n", 0.000000001D),
        MICRO("Micro", "u", 0.000001D),
        MILLI("Milli", "m", 0.001D),
        BASE("", "", 1),
        KILO("Kilo", "k", 1_000D),
        MEGA("Mega", "M", 1_000_000D),
        GIGA("Giga", "G", 1_000_000_000D),
        TERA("Tera", "T", 1_000_000_000_000D),
        PETA("Peta", "P", 1_000_000_000_000_000D),
        EXA("Exa", "E", 1_000_000_000_000_000_000D),
        ZETTA("Zetta", "Z", 1_000_000_000_000_000_000_000D),
        YOTTA("Yotta", "Y", 1_000_000_000_000_000_000_000_000D);

        /**
         * long name for the unit
         */
        private final String name;

        /**
         * short unit version of the unit
         */
        private final String symbol;

        /**
         * Point by which a number is consider to be of this unit
         */
        private final double value;

        MeasurementUnit(String name, String symbol, double value) {
            this.name = name;
            this.symbol = symbol;
            this.value = value;
        }

        public String getName(boolean getShort) {
            if (getShort) {
                return symbol;
            }
            return name;
        }

        public double process(double d) {
            return d / value;
        }

        public boolean above(double d) {
            return d > value;
        }

        public boolean below(double d) {
            return d < value;
        }
    }

    /**
     * Metric system of measurement.
     */
    public enum FloatingLongMeasurementUnit {
        MILLI("Milli", "m", FloatingLong.createConst(.001)),
        BASE("", "", FloatingLong.ONE),
        KILO("Kilo", "k", FloatingLong.createConst(1_000)),
        MEGA("Mega", "M", FloatingLong.createConst(1_000_000)),
        GIGA("Giga", "G", FloatingLong.createConst(1_000_000_000)),
        TERA("Tera", "T", FloatingLong.createConst(1_000_000_000_000L)),
        PETA("Peta", "P", FloatingLong.createConst(1_000_000_000_000_000L)),
        EXA("Exa", "E", FloatingLong.createConst(1_000_000_000_000_000_000L));

        /**
         * long name for the unit
         */
        private final String name;

        /**
         * short unit version of the unit
         */
        private final String symbol;

        /**
         * Point by which a number is consider to be of this unit
         */
        private final FloatingLong value;

        FloatingLongMeasurementUnit(String name, String symbol, FloatingLong value) {
            this.name = name;
            this.symbol = symbol;
            this.value = value;
        }

        public String getName(boolean getShort) {
            if (getShort) {
                return symbol;
            }
            return name;
        }

        public FloatingLong process(FloatingLong d) {
            return d.divide(value);
        }

        public boolean above(FloatingLong d) {
            return d.greaterThan(value);
        }

        public boolean below(FloatingLong d) {
            return d.smallerThan(value);
        }
    }

    public enum EnergyType implements IIncrementalEnum<EnergyType>, IHasTranslationKey {
        J(MekanismLang.ENERGY_JOULES_SHORT),
        FE(MekanismLang.ENERGY_FORGE_SHORT),
        EU(MekanismLang.ENERGY_EU_SHORT);

        private static final EnergyType[] TYPES = values();
        private final ILangEntry langEntry;

        EnergyType(ILangEntry langEntry) {
            this.langEntry = langEntry;
        }

        @Override
        public String getTranslationKey() {
            return langEntry.getTranslationKey();
        }

        @Nonnull
        @Override
        public EnergyType byIndex(int index) {
            //TODO: Is it more efficient to check if index is negative and then just do the normal mod way?
            return TYPES[Math.floorMod(index, TYPES.length)];
        }
    }

    public enum TempType implements IIncrementalEnum<TempType>, IHasTranslationKey {
        K(MekanismLang.TEMPERATURE_KELVIN_SHORT),
        C(MekanismLang.TEMPERATURE_CELSIUS_SHORT),
        R(MekanismLang.TEMPERATURE_RANKINE_SHORT),
        F(MekanismLang.TEMPERATURE_FAHRENHEIT_SHORT),
        STP(MekanismLang.TEMPERATURE_AMBIENT_SHORT);

        private static final TempType[] TYPES = values();
        private final ILangEntry langEntry;

        TempType(ILangEntry langEntry) {
            this.langEntry = langEntry;
        }

        @Override
        public String getTranslationKey() {
            return langEntry.getTranslationKey();
        }

        @Nonnull
        @Override
        public TempType byIndex(int index) {
            //TODO: Is it more efficient to check if index is negative and then just do the normal mod way?
            return TYPES[Math.floorMod(index, TYPES.length)];
        }
    }
}