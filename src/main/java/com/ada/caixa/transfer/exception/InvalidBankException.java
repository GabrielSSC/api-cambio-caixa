package com.ada.caixa.transfer.exception;

/** Bank code not found via BrasilAPI. Mapped to 422. */
public class InvalidBankException extends RuntimeException {
    public InvalidBankException(String message) {
        super(message);
    }
}
