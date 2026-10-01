package com.ada.caixa.transfer.cambio.service;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ada.caixa.transfer.cambio.client.AwesomeApiClient;
import com.ada.caixa.transfer.cambio.domain.Cotacao;
import com.ada.caixa.transfer.cambio.dto.CotacaoResponseDTO;
import com.ada.caixa.transfer.exception.MoedaNaoSuportadaException;

@ExtendWith(MockitoExtension.class)
class CotacaoServiceTest {

    @Mock
    private AwesomeApiClient awesomeApiClient;

    @InjectMocks
    private CotacaoService cotacaoService;

    @Test
    void deveConsultarCotacaoDeMoedaSuportada() {
        when(awesomeApiClient.consultar("USD"))
                .thenReturn(new Cotacao("USD", "BRL", new BigDecimal("5.2235")));

        CotacaoResponseDTO resposta = cotacaoService.consultar("usd");

        assertEquals("USD", resposta.moeda());
        assertEquals("BRL", resposta.moedaBase());
        assertEquals(new BigDecimal("5.2235"), resposta.valorCotacao());
        assertNotNull(resposta.consultadoEm());
        verify(awesomeApiClient).consultar("USD");
    }

    @Test
    void deveRejeitarMoedaNaoSuportadaSemChamarApiExterna() {
        assertThrows(MoedaNaoSuportadaException.class,
                () -> cotacaoService.consultar("GBP"));

        verifyNoInteractions(awesomeApiClient);
    }
}