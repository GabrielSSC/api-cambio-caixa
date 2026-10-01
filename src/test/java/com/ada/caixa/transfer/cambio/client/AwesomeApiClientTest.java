package com.ada.caixa.transfer.cambio.client;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.ada.caixa.transfer.cambio.domain.Cotacao;
import com.ada.caixa.transfer.exception.ExternalApiUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AwesomeApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AwesomeApiClient awesomeApiClient;

    @Test
    void deveConverterRespostaDaAwesomeApiParaCotacaoInterna() throws Exception {
        JsonNode resposta = new ObjectMapper().readTree("""
                {"USDBRL":{"ask":"5.2235"}}
                """);
        when(restTemplate.getForObject(
                eq("https://economia.awesomeapi.com.br/json/last/{par}"),
                eq(JsonNode.class),
                eq("USD-BRL")))
                .thenReturn(resposta);

        Cotacao cotacao = awesomeApiClient.consultar("USD");

        assertEquals("USD", cotacao.moeda());
        assertEquals("BRL", cotacao.moedaBase());
        assertEquals(new BigDecimal("5.2235"), cotacao.valor());
    }

    @Test
    void deveConverterFalhaDeConexaoEmExcecaoDaAplicacao() {
        when(restTemplate.getForObject(
                eq("https://economia.awesomeapi.com.br/json/last/{par}"),
                eq(JsonNode.class),
                eq("EUR-BRL")))
                .thenThrow(new ResourceAccessException("timeout"));

        assertThrows(ExternalApiUnavailableException.class,
                () -> awesomeApiClient.consultar("EUR"));
    }
}