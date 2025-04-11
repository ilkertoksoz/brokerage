package com.tr.ing.brokerage.exception;

public class AssetAlreadyExistException extends RuntimeException {
    public AssetAlreadyExistException(String message) {
        super(message);
    }
}
