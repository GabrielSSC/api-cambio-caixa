package com.ada.caixa.transfer.cambio.service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.ada.caixa.transfer.cambio.client.CotacaoClient;
import com.ada.caixa.transfer.cambio.domain.Cotacao;
import com.ada.caixa.transfer.cambio.dto.CotacaoResponseDTO;
import com.ada.caixa.transfer.exception.MoedaNaoSuportadaException;

@Service
public class CotacaoService {

    private static final Set<String> MOEDAS_SUPORTADAS = Set.of("USD", "EUR");

    private final CotacaoClient cotacaoClient;

    public CotacaoService(CotacaoClient cotacaoClient) {
        this.cotacaoClient = cotacaoClient;
    }

    public CotacaoResponseDTO consultar(String moedaRecebida) {
        String moeda = moedaRecebida.trim().toUpperCase(Locale.ROOT);

        if (!MOEDAS_SUPORTADAS.contains(moeda)) {
            throw new MoedaNaoSuportadaException(
                    "Moeda '" + moeda + "' não é suportada. Utilize USD ou EUR.");
        }

        Cotacao cotacao = cotacaoClient.consultar(moeda);
        return CotacaoResponseDTO.fromDomain(cotacao, LocalDateTime.now());
    }
}