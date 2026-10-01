package com.ada.caixa.transfer.cambio.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ada.caixa.transfer.cambio.dto.CotacaoResponseDTO;
import com.ada.caixa.transfer.cambio.service.CotacaoService;
import com.ada.caixa.transfer.config.SecurityConfig;
import com.ada.caixa.transfer.exception.MoedaNaoSuportadaException;
import com.ada.caixa.transfer.web.GlobalExceptionHandler;

@WebMvcTest(CotacaoController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class CotacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CotacaoService cotacaoService;

    @Test
    void deveRetornarCotacaoComSucesso() throws Exception {
        when(cotacaoService.consultar("USD")).thenReturn(new CotacaoResponseDTO(
                "USD", "BRL", new BigDecimal("5.2235"), LocalDateTime.parse("2026-09-30T12:00:00")));

        mockMvc.perform(get("/api/cambio/cotacao/USD")
                        .with(user("instructor").roles("INSTRUCTOR"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moeda").value("USD"))
                .andExpect(jsonPath("$.moedaBase").value("BRL"))
                .andExpect(jsonPath("$.valorCotacao").value(5.2235))
                .andExpect(jsonPath("$.consultadoEm").exists());
    }

    @Test
    void deveRetornar422ParaMoedaNaoSuportada() throws Exception {
        when(cotacaoService.consultar("GBP"))
                .thenThrow(new MoedaNaoSuportadaException(
                        "Moeda 'GBP' não é suportada. Utilize USD ou EUR."));

        mockMvc.perform(get("/api/cambio/cotacao/GBP")
                        .with(user("instructor").roles("INSTRUCTOR")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(
                        "Moeda 'GBP' não é suportada. Utilize USD ou EUR."));
    }
}