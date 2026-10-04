package com.ada.caixa.transfer.compra.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ada.caixa.transfer.cambio.service.CotacaoService;
import com.ada.caixa.transfer.cliente.dto.ClienteResponseDTO;
import com.ada.caixa.transfer.cliente.service.ClienteService;
import com.ada.caixa.transfer.compra.domain.OrdemCompra;
import com.ada.caixa.transfer.compra.dto.OrdemCompraRequestDTO;
import com.ada.caixa.transfer.compra.dto.OrdemCompraResponseDTO;
import com.ada.caixa.transfer.compra.repository.OrdemCompraRepository;
import com.ada.caixa.transfer.exception.OrdemCompraNotFoundException;

@Service
public class OrdemCompraService {

    private final ClienteService clienteService;
    private final CotacaoService cotacaoService;
    private final OrdemCompraRepository ordemCompraRepository;

    public OrdemCompraService(ClienteService clienteService, CotacaoService cotacaoService,
            OrdemCompraRepository ordemCompraRepository) {
        this.clienteService = clienteService;
        this.cotacaoService = cotacaoService;
        this.ordemCompraRepository = ordemCompraRepository;
    }

    @Transactional
    public OrdemCompraResponseDTO registrar(OrdemCompraRequestDTO request) {
        ClienteResponseDTO cliente = clienteService.consultarPorCpf(request.cpf());
        var cotacao = cotacaoService.consultar(request.tipoMoeda());
        BigDecimal total = cotacao.valorCotacao()
                .multiply(request.valorMoedaEstrangeira())
                .setScale(2, RoundingMode.HALF_UP);

        OrdemCompra ordemCompra = new OrdemCompra(
                cliente.idCliente(),
                cliente.cpf(),
                LocalDateTime.now(),
                cotacao.moeda(),
                request.valorMoedaEstrangeira(),
                cotacao.valorCotacao(),
                total,
                request.numeroAgenciaRetirada()
        );

        return OrdemCompraResponseDTO.fromEntity(ordemCompraRepository.save(ordemCompra));
    }

    @Transactional(readOnly = true)
    public OrdemCompraResponseDTO consultarPorId(Long id) {
        OrdemCompra ordemCompra = ordemCompraRepository.findById(id)
                .orElseThrow(() -> new OrdemCompraNotFoundException(
                        "Ordem de compra com ID " + id + " não encontrada."));

        return OrdemCompraResponseDTO.fromEntity(ordemCompra);
    }

    @Transactional(readOnly = true)
    public List<OrdemCompraResponseDTO> consultarHistoricoPorCpf(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");

        return ordemCompraRepository.findAllByCpfClienteOrderByDataSolicitacaoDesc(cpfLimpo)
                .stream()
                .map(OrdemCompraResponseDTO::fromEntity)
                .toList();
    }
}
