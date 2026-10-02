package com.ada.caixa.transfer.compra.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ada.caixa.transfer.compra.domain.OrdemCompra;
import com.fasterxml.jackson.annotation.JsonProperty;

public record OrdemCompraResponseDTO(
        @JsonProperty("id_compra") Long idCompra,
        @JsonProperty("id_cliente") Long idCliente,
        @JsonProperty("cpf_cliente") String cpfCliente,
        LocalDateTime dataSolicitacao,
        @JsonProperty("tipo_moeda") String tipoMoeda,
        @JsonProperty("valor_moeda_estrangeira") BigDecimal valorMoedaEstrangeira,
        @JsonProperty("valor_cotacao") BigDecimal valorCotacao,
        @JsonProperty("valor_total_operacao") BigDecimal valorTotalOperacao,
        @JsonProperty("numero_agencia_retirada") String numeroAgenciaRetirada
) {
    public static OrdemCompraResponseDTO fromEntity(OrdemCompra ordemCompra) {
        return new OrdemCompraResponseDTO(
                ordemCompra.getId(),
                ordemCompra.getIdCliente(),
                ordemCompra.getCpfCliente(),
                ordemCompra.getDataSolicitacao(),
                ordemCompra.getTipoMoeda(),
                ordemCompra.getValorMoedaEstrangeira(),
                ordemCompra.getValorCotacao(),
                ordemCompra.getValorTotalOperacao(),
                ordemCompra.getNumeroAgenciaRetirada()
        );
    }
}
