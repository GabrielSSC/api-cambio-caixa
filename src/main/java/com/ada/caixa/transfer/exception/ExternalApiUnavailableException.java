package com.ada.caixa.transfer.exception;

/** Falha ou timeout ao consultar a API externa. Mapeada para 503. */
public class ExternalApiUnavailableException extends RuntimeException {
    public ExternalApiUnavailableException(String message) {
        super(message);
    }
}
