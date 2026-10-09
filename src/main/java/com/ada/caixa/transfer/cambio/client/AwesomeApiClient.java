package com.ada.caixa.transfer.cambio.client;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ada.caixa.transfer.cambio.domain.Cotacao;
import com.ada.caixa.transfer.exception.ExternalApiUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class AwesomeApiClient implements CotacaoClient {

    private static final String URL = "https://economia.awesomeapi.com.br/json/last/{par}";
    private static final String ERRO_API =
            "Não foi possível obter a cotação no momento. Tente novamente.";

    private final RestTemplate restTemplate;

    public AwesomeApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Cotacao consultar(String moeda) {
        try {
            String par = moeda + "-BRL";
            JsonNode resposta = restTemplate.getForObject(URL, JsonNode.class, par);
            JsonNode dadosCotacao = resposta == null ? null : resposta.get(moeda + "BRL");

            if (dadosCotacao == null || dadosCotacao.path("ask").asText().isBlank()) {
                throw new ExternalApiUnavailableException(ERRO_API);
            }

            BigDecimal valorVenda = new BigDecimal(dadosCotacao.path("ask").asText());
            return new Cotacao(moeda, "BRL", valorVenda);
        } catch (RestClientException | NumberFormatException ex) {
            throw new ExternalApiUnavailableException(ERRO_API);
        }
    }
}