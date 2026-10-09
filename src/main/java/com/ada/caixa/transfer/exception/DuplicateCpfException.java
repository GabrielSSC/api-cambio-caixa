package com.ada.caixa.transfer.exception;

/** CPF já cadastrado para um cliente. Mapeada para 409. */
public class DuplicateCpfException extends RuntimeException {
    public DuplicateCpfException(String message) {
        super(message);
    }
}
