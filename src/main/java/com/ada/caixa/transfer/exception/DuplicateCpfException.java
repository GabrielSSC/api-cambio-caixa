package com.ada.caixa.transfer.exception;

/** CPF already has an account registered. Mapped to 409. */
public class DuplicateCpfException extends RuntimeException {
    public DuplicateCpfException(String message) {
        super(message);
    }
}
