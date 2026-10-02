package com.ada.caixa.transfer.compra;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ada.caixa.transfer.cambio.dto.CotacaoResponseDTO;
import com.ada.caixa.transfer.cambio.service.CotacaoService;
import com.ada.caixa.transfer.cliente.domain.EstadoCivil;
import com.ada.caixa.transfer.cliente.domain.Sexo;
import com.ada.caixa.transfer.cliente.dto.ClienteResponseDTO;
import com.ada.caixa.transfer.cliente.service.ClienteService;
import com.ada.caixa.transfer.compra.domain.OrdemCompra;
import com.ada.caixa.transfer.compra.dto.OrdemCompraRequestDTO;
import com.ada.caixa.transfer.compra.dto.OrdemCompraResponseDTO;
import com.ada.caixa.transfer.compra.repository.OrdemCompraRepository;
import com.ada.caixa.transfer.compra.service.OrdemCompraService;

@ExtendWith(MockitoExtension.class)
class OrdemCompraServiceTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private CotacaoService cotacaoService;

    @Mock
    private OrdemCompraRepository ordemCompraRepository;

    @InjectMocks
    private OrdemCompraService ordemCompraService;

    @Test
    void deveCalcularSalvarEDevolverOrdemDeCompra() {
        OrdemCompraRequestDTO request = new OrdemCompraRequestDTO(
                "43488428095", "EUR", new BigDecimal("100.00"), "7057");
        ClienteResponseDTO cliente = new ClienteResponseDTO(
                1L, "Maria Souza", "43488428095", LocalDate.of(1990, 5, 15),
                EstadoCivil.SOLTEIRO, Sexo.FEMININO);
        when(clienteService.consultarPorCpf("43488428095")).thenReturn(cliente);
        when(cotacaoService.consultar("EUR")).thenReturn(new CotacaoResponseDTO(
                "EUR", "BRL", new BigDecimal("6.5857"), null));
        when(ordemCompraRepository.save(any(OrdemCompra.class))).thenAnswer(invocation -> {
            OrdemCompra ordem = invocation.getArgument(0);
            ordem.setId(10L);
            return ordem;
        });

        OrdemCompraResponseDTO resposta = ordemCompraService.registrar(request);

        assertEquals(10L, resposta.idCompra());
        assertEquals(1L, resposta.idCliente());
        assertEquals("43488428095", resposta.cpfCliente());
        assertEquals(new BigDecimal("658.57"), resposta.valorTotalOperacao());
        assertEquals("7057", resposta.numeroAgenciaRetirada());
        verify(clienteService).consultarPorCpf("43488428095");
        verify(cotacaoService).consultar("EUR");
        verify(ordemCompraRepository).save(any(OrdemCompra.class));
    }
}
