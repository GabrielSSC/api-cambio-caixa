package com.ada.caixa.transfer.cambio.domain;

import java.math.BigDecimal;

/** Internal representation of a currency price in a base currency. */
public record Cotacao(String moeda, String moedaBase, BigDecimal valor) {
}