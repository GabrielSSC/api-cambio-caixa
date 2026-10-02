package com.ada.caixa.transfer.compra.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ada.caixa.transfer.compra.dto.OrdemCompraRequestDTO;
import com.ada.caixa.transfer.compra.dto.OrdemCompraResponseDTO;
import com.ada.caixa.transfer.compra.service.OrdemCompraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Compras", description = "Registro de ordens de compra de moeda estrangeira")
@RestController
@RequestMapping("/api/compras")
public class OrdemCompraController {

    private final OrdemCompraService ordemCompraService;

    public OrdemCompraController(OrdemCompraService ordemCompraService) {
        this.ordemCompraService = ordemCompraService;
    }

    @Operation(summary = "UC4 · Registrar ordem de compra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordem de compra registrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou agência fora do formato"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "422", description = "Moeda não suportada")
    })
    @PostMapping
    public ResponseEntity<OrdemCompraResponseDTO> registrar(@RequestBody @Valid OrdemCompraRequestDTO request) {
        OrdemCompraResponseDTO response = ordemCompraService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "UC5 · Consultar histórico de compras por CPF")
    @ApiResponse(responseCode = "200", description = "Histórico encontrado ou lista vazia")
    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<OrdemCompraResponseDTO>> consultarHistorico(@PathVariable String cpf) {
        return ResponseEntity.ok(ordemCompraService.consultarHistoricoPorCpf(cpf));
    }
}
