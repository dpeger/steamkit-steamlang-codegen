package de.peger.steamkit.steamlang.compiler.domain;

/**
 * @author dpeger
 *
 */
public enum SteamdSerializationConstant {

    CONST("const"),

    PROTO("proto", "proto<\\w+>"),

    PROTOMASK("protomask"),

    PROTOMASKGC("protomaskgc"),

    STEAMID("steamidmarshal"),

    GAMEID("gameidmarshal"),

    BOOL("boolmarshal");

    private final String mValue;

    private final String mPattern;

    private SteamdSerializationConstant(final String pValue) {
        mValue = pValue;
        mPattern = pValue;
    }

    private SteamdSerializationConstant(final String pValue, final String pPattern) {
        mValue = pValue;
        mPattern = pPattern;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return mValue;
    }

    public static SteamdSerializationConstant forValue(final String pValue) {

        for (final SteamdSerializationConstant tEnum : SteamdSerializationConstant.values()) {
            if (pValue.matches(tEnum.mPattern)) {
                return tEnum;
            }
        }

        throw new IllegalArgumentException(
                "No " + SteamdSerializationConstant.class.getName() + " with value '" + pValue + "'");
    }
}
