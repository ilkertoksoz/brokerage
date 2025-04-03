package com.tr.ing.brokerage.exception;

public class InsufficientAssetException extends RuntimeException {
    public InsufficientAssetException(String message) {
        super(message);
    }
}
