package com.tr.ing.brokerage.enums;

import lombok.Getter;

@Getter
public enum Side {
    BUY("Alış", "001"),
    SELL("Satış", "002");

    private final String name;
    private final String code;

    Side(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static Side fromValue(String value) {
        for (Side side : Side.values()) {
            if (side.name.equals(value) || side.code.equals(value)) {
                return side;
            }
        }

        throw new IllegalArgumentException("Geçersiz işlem yönü: " + value);
    }
}