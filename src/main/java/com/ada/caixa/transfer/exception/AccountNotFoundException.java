package com.ada.caixa.transfer.exception;

/** Account or CPF not found in the database. Mapped to 404. */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
