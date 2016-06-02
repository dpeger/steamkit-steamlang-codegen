package de.peger.steamkit.steamlang.compiler.domain;

import java.util.Objects;

/**
 * @author dpeger
 *
 */
public enum SteamdTypeConstant {

    ULONG("ulong"),

    LONG("long"),

    INT("int"),

    UINT("uint"),

    SHORT("short"),

    USHORT("ushort"),

    BYTE("byte");

    private final String mValue;

    private SteamdTypeConstant(final String pValue) {
        mValue = pValue;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return mValue;
    }

    public static SteamdTypeConstant forValue(final String pValue) {

        for (final SteamdTypeConstant tEnum : SteamdTypeConstant.values()) {
            if (Objects.equals(tEnum.getValue(), pValue)) {
                return tEnum;
            }
        }

        throw new IllegalArgumentException("No " + SteamdTypeConstant.class.getName() + " with value '" + pValue + "'");
    }
}
