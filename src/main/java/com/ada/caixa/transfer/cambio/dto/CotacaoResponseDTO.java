package com.ada.caixa.transfer.cambio.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ada.caixa.transfer.cambio.domain.Cotacao;

public record CotacaoResponseDTO(
        String moeda,
        String moedaBase,
        BigDecimal valorCotacao,
        LocalDateTime consultadoEm
) {
    public static CotacaoResponseDTO fromDomain(Cotacao cotacao, LocalDateTime consultadoEm) {
        return new CotacaoResponseDTO(
                cotacao.moeda(),
                cotacao.moedaBase(),
                cotacao.valor(),
                consultadoEm
        );
    }
}