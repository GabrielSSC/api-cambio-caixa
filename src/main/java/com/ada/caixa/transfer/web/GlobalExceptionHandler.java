package com.ada.caixa.transfer.web;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ada.caixa.transfer.exception.AccountNotFoundException;
import com.ada.caixa.transfer.exception.ClienteNotFoundException;
import com.ada.caixa.transfer.exception.DuplicateCpfException;
import com.ada.caixa.transfer.exception.ExternalApiUnavailableException;
import com.ada.caixa.transfer.exception.InvalidBankException;
import com.ada.caixa.transfer.exception.InvalidOperationException;
import com.ada.caixa.transfer.exception.MoedaNaoSuportadaException;
import com.ada.caixa.transfer.exception.TransferNotFoundException;

/** Converts every business exception into a consistent JSON response. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(AccountNotFoundException ex, WebRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleClienteNotFound(ClienteNotFoundException ex, WebRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(TransferNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTransferNotFound(TransferNotFoundException ex, WebRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(DuplicateCpfException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(DuplicateCpfException ex, WebRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidBankException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidBank(InvalidBankException ex, WebRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    @ExceptionHandler(MoedaNaoSuportadaException.class)
    public ResponseEntity<ApiErrorResponse> handleMoedaNaoSuportada(
            MoedaNaoSuportadaException ex, WebRequest req) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    @ExceptionHandler(ExternalApiUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalApi(ExternalApiUnavailableException ex, WebRequest req) {
        return build(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidOperation(InvalidOperationException ex, WebRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message, req);
    }

    private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message, WebRequest req) {
        String path = req.getDescription(false).replace("uri=", "");
        ApiErrorResponse body = new ApiErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message, path);
        return ResponseEntity.status(status).body(body);
    }
}
