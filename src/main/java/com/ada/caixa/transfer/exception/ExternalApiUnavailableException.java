package com.ada.caixa.transfer.exception;

/** Failure or timeout while calling BrasilAPI. Mapped to 503. */
public class ExternalApiUnavailableException extends RuntimeException {
    public ExternalApiUnavailableException(String message) {
        super(message);
    }
}
