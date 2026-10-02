package com.ada.caixa.transfer.compra.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dados para registrar uma ordem de compra de moeda")
public record OrdemCompraRequestDTO(
        @Schema(description = "CPF do cliente, com 11 dígitos", example = "43488428095")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos")
        String cpf,

        @Schema(description = "Moeda estrangeira desejada: USD ou EUR", example = "EUR")
        @NotBlank(message = "O tipo de moeda é obrigatório")
        String tipoMoeda,

        @Schema(description = "Quantidade desejada na moeda estrangeira", example = "100.00")
        @NotNull(message = "O valor da moeda estrangeira é obrigatório")
        @DecimalMin(value = "0", inclusive = false, message = "O valor deve ser maior que zero")
        BigDecimal valorMoedaEstrangeira,

        @Schema(description = "Agência onde a compra será retirada, com 4 dígitos", example = "7057")
        @NotBlank(message = "O número da agência é obrigatório")
        @Pattern(regexp = "\\d{4}", message = "Número de agência inválido.")
        String numeroAgenciaRetirada
) {
}
