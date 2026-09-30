package com.ada.caixa.transfer.cliente.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ada.caixa.transfer.cliente.dto.ClienteRequestDTO;
import com.ada.caixa.transfer.cliente.dto.ClienteResponseDTO;
import com.ada.caixa.transfer.cliente.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Clientes", description = "Endpoints de gerenciamento e cadastro de clientes")
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "UC1 · Cadastrar cliente", description = "Cadastra um novo cliente caso o CPF ainda não exista na base.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos (formato de CPF, campos obrigatórios, etc.)"),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado na base de dados")
    })
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> cadastrar(
            @RequestBody @Valid ClienteRequestDTO request,
            UriComponentsBuilder uriBuilder) {

        ClienteResponseDTO response = clienteService.cadastrar(request);

        URI location = uriBuilder.path("/api/clientes/{cpf}")
                .buildAndExpand(response.cpf())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "UC2 · Consultar cliente por CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteResponseDTO> consultarPorCpf(@PathVariable String cpf) {
        ClienteResponseDTO response = clienteService.consultarPorCpf(cpf);
        return ResponseEntity.ok(response);
    }
}
