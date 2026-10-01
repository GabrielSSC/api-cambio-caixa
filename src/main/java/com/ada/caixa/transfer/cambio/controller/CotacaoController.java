package com.ada.caixa.transfer.cambio.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ada.caixa.transfer.cambio.dto.CotacaoResponseDTO;
import com.ada.caixa.transfer.cambio.service.CotacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Câmbio", description = "Consulta de cotações de moedas")
@RestController
@RequestMapping("/api/cambio")
public class CotacaoController {

    private final CotacaoService cotacaoService;

    public CotacaoController(CotacaoService cotacaoService) {
        this.cotacaoService = cotacaoService;
    }

    @Operation(summary = "UC3 · Consultar cotação de moeda")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cotação consultada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Moeda não suportada"),
            @ApiResponse(responseCode = "503", description = "Serviço externo indisponível")
    })
    @GetMapping("/cotacao/{moeda}")
    public ResponseEntity<CotacaoResponseDTO> consultar(@PathVariable String moeda) {
        return ResponseEntity.ok(cotacaoService.consultar(moeda));
    }
}