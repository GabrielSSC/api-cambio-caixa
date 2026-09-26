package com.ada.caixa.transfer.exception;

/** Transfer id not found in the database. Mapped to 404. */
public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(String message) {
        super(message);
    }
}
