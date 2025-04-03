package com.tr.ing.brokerage.enums;

import lombok.Getter;

@Getter
public enum Status {
    PENDING("Beklemede", "001"),
    MATCHED("Eşleşmiş", "002"),
    CANCELED("İptal Edilmiş", "003");

    private final String name;
    private final String code;

    Status(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static Status fromValue(String value) {
        for (Status status : Status.values()) {
            if (status.name.equals(value)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Geçersiz durum: " + value);
    }
}
