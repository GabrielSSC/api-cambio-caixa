package com.ada.caixa.transfer.compra.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "compras")
public class OrdemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "cpf_cliente", nullable = false, length = 11)
    private String cpfCliente;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "tipo_moeda", nullable = false, length = 3)
    private String tipoMoeda;

    @Column(name = "valor_moeda_estrangeira", nullable = false, precision = 19, scale = 4)
    private BigDecimal valorMoedaEstrangeira;

    @Column(name = "valor_cotacao", nullable = false, precision = 19, scale = 6)
    private BigDecimal valorCotacao;

    @Column(name = "valor_total_operacao", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorTotalOperacao;

    @Column(name = "numero_agencia_retirada", nullable = false, length = 4)
    private String numeroAgenciaRetirada;

    protected OrdemCompra() {
    }

    public OrdemCompra(Long idCliente, String cpfCliente, LocalDateTime dataSolicitacao,
            String tipoMoeda, BigDecimal valorMoedaEstrangeira, BigDecimal valorCotacao,
            BigDecimal valorTotalOperacao, String numeroAgenciaRetirada) {
        this.idCliente = idCliente;
        this.cpfCliente = cpfCliente;
        this.dataSolicitacao = dataSolicitacao;
        this.tipoMoeda = tipoMoeda;
        this.valorMoedaEstrangeira = valorMoedaEstrangeira;
        this.valorCotacao = valorCotacao;
        this.valorTotalOperacao = valorTotalOperacao;
        this.numeroAgenciaRetirada = numeroAgenciaRetirada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public String getTipoMoeda() {
        return tipoMoeda;
    }

    public BigDecimal getValorMoedaEstrangeira() {
        return valorMoedaEstrangeira;
    }

    public BigDecimal getValorCotacao() {
        return valorCotacao;
    }

    public BigDecimal getValorTotalOperacao() {
        return valorTotalOperacao;
    }

    public String getNumeroAgenciaRetirada() {
        return numeroAgenciaRetirada;
    }
}
