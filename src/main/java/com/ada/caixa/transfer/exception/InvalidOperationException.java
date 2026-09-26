package com.ada.caixa.transfer.exception;

/** Business rule violated (e.g. CPF does not match the source account). Mapped to 400. */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
