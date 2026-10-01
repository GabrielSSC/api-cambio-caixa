package com.ada.caixa.transfer.exception;

public class MoedaNaoSuportadaException extends RuntimeException {
    public MoedaNaoSuportadaException(String message) {
        super(message);
    }
}