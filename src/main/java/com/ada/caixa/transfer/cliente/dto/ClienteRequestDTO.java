package com.ada.caixa.transfer.cliente.dto;

import java.time.LocalDate;

import com.ada.caixa.transfer.cliente.domain.EstadoCivil;
import com.ada.caixa.transfer.cliente.domain.Sexo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dados para cadastro de um novo cliente")
public record ClienteRequestDTO(

    @Schema(description = "Nome completo do cliente", example = "Maria Souza")
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @Schema(description = "CPF do cliente (11 dígitos, com ou sem pontuação)", example = "43488428095")
    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^(\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})$", message = "O CPF deve conter 11 dígitos numéricos válidos")
    String cpf,

    @Schema(description = "Data de nascimento no formato AAAA-MM-DD", example = "1990-05-15")
    @NotNull(message = "A data de nascimento é obrigatória")
    @Past(message = "A data de nascimento deve ser uma data no passado")
    LocalDate dataNascimento,

    @Schema(description = "Estado civil do cliente", example = "SOLTEIRO")
    @NotNull(message = "O estado civil é obrigatório")
    EstadoCivil estadoCivil,

    @Schema(description = "Sexo do cliente", example = "FEMININO")
    @NotNull(message = "O sexo é obrigatório")
    Sexo sexo
) {
    /** Retorna apenas os dígitos do CPF, removendo eventuais pontos e traços. */
    public String getCpfLimpo() {
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
    }
}
