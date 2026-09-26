package com.ada.caixa.transfer.cliente.dto;

import java.time.LocalDate;

import com.ada.caixa.transfer.cliente.domain.Cliente;
import com.ada.caixa.transfer.cliente.domain.EstadoCivil;
import com.ada.caixa.transfer.cliente.domain.Sexo;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do cliente cadastrado")
public record ClienteResponseDTO(

    @Schema(description = "Identificador único gerado para o cliente", example = "1")
    @JsonProperty("id_cliente")
    Long idCliente,

    @Schema(description = "Nome completo do cliente", example = "Maria Souza")
    String nome,

    @Schema(description = "CPF do cliente", example = "43488428095")
    String cpf,

    @Schema(description = "Data de nascimento", example = "1990-05-15")
    LocalDate dataNascimento,

    @Schema(description = "Estado civil do cliente", example = "SOLTEIRO")
    EstadoCivil estadoCivil,

    @Schema(description = "Sexo do cliente", example = "FEMININO")
    Sexo sexo
) {
    public static ClienteResponseDTO fromEntity(Cliente cliente) {
        return new ClienteResponseDTO(
            cliente.getId(),
            cliente.getNome(),
            cliente.getCpf(),
            cliente.getDataNascimento(),
            cliente.getEstadoCivil(),
            cliente.getSexo()
        );
    }
}
