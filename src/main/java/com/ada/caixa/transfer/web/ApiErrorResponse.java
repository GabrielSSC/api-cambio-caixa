package com.ada.caixa.transfer.web;

import java.time.Instant;

/** Standard error body returned by the whole API — never a raw stack trace. */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
