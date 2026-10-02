package com.ada.caixa.transfer.compra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.ada.caixa.transfer.cambio.client.AwesomeApiClient;
import com.ada.caixa.transfer.cambio.domain.Cotacao;
import com.ada.caixa.transfer.cliente.domain.Cliente;
import com.ada.caixa.transfer.cliente.domain.EstadoCivil;
import com.ada.caixa.transfer.cliente.domain.Sexo;
import com.ada.caixa.transfer.cliente.repository.ClienteRepository;
import com.ada.caixa.transfer.compra.domain.OrdemCompra;
import com.ada.caixa.transfer.compra.repository.OrdemCompraRepository;

@SpringBootTest
@AutoConfigureMockMvc
class OrdemCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private OrdemCompraRepository ordemCompraRepository;

    @MockitoBean
    private AwesomeApiClient awesomeApiClient;

        @BeforeEach
        void limparDados() {
                ordemCompraRepository.deleteAll();
                clienteRepository.deleteAll();
        }

    @Test
    @WithMockUser
    void deveRegistrarCompraAutenticadaPersistirEResponder201() throws Exception {
                Cliente cliente = salvarCliente();
        when(awesomeApiClient.consultar("EUR"))
                .thenReturn(new Cotacao("EUR", "BRL", new BigDecimal("6.5857")));

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "cpf": "43488428095",
                                  "tipoMoeda": "EUR",
                                  "valorMoedaEstrangeira": 100.00,
                                  "numeroAgenciaRetirada": "7057"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id_compra").isNumber())
                .andExpect(jsonPath("$.id_cliente").value(cliente.getId()))
                .andExpect(jsonPath("$.cpf_cliente").value("43488428095"))
                .andExpect(jsonPath("$.tipo_moeda").value("EUR"))
                .andExpect(jsonPath("$.valor_total_operacao").value(658.57))
                .andExpect(jsonPath("$.numero_agencia_retirada").value("7057"));

                assertEquals(1, ordemCompraRepository.count());
        }

        @Test
        @WithMockUser
        void deveConsultarHistoricoDoClienteOrdenadoDoMaisNovoParaOMaisAntigo() throws Exception {
                salvarCliente();
                OrdemCompra compraAntiga = salvarOrdem("USD", LocalDateTime.of(2026, 9, 1, 10, 0));
                OrdemCompra compraRecente = salvarOrdem("EUR", LocalDateTime.of(2026, 9, 2, 10, 0));

                mockMvc.perform(get("/api/compras/cliente/434.884.280-95"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id_compra").value(compraRecente.getId()))
                                .andExpect(jsonPath("$[0].tipo_moeda").value("EUR"))
                                .andExpect(jsonPath("$[1].id_compra").value(compraAntiga.getId()))
                                .andExpect(jsonPath("$[1].tipo_moeda").value("USD"));
        }

        @Test
        @WithMockUser
        void deveRetornarListaVaziaQuandoClienteNaoTiverCompras() throws Exception {
                mockMvc.perform(get("/api/compras/cliente/43488428095"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithMockUser
        void deveResponder400QuandoAgenciaNaoTiverQuatroDigitos() throws Exception {
                mockMvc.perform(post("/api/compras")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(corpoCompra("43488428095", "EUR", "100.00", "75")))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        void deveResponder404QuandoClienteNaoExistir() throws Exception {
                mockMvc.perform(post("/api/compras")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(corpoCompra("43488428095", "EUR", "100.00", "7057")))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        void deveResponder422QuandoMoedaNaoForSuportada() throws Exception {
                salvarCliente();

                mockMvc.perform(post("/api/compras")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(corpoCompra("43488428095", "GBP", "100.00", "7057")))
                                .andExpect(status().isUnprocessableEntity());
        }

        @Test
        void deveExigirAutenticacaoParaRegistrarCompra() throws Exception {
                mockMvc.perform(post("/api/compras")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(corpoCompra("43488428095", "EUR", "100.00", "7057")))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        void deveExigirAutenticacaoParaConsultarHistorico() throws Exception {
                mockMvc.perform(get("/api/compras/cliente/43488428095"))
                                .andExpect(status().isUnauthorized());
        }

        private Cliente salvarCliente() {
                return clienteRepository.save(new Cliente(
                                "Maria Souza", "43488428095", LocalDate.of(1990, 5, 15),
                                EstadoCivil.SOLTEIRO, Sexo.FEMININO));
        }

        private String corpoCompra(String cpf, String moeda, String valor, String agencia) {
                return """
                                {
                                  "cpf": "%s",
                                  "tipoMoeda": "%s",
                                  "valorMoedaEstrangeira": %s,
                                  "numeroAgenciaRetirada": "%s"
                                }
                                """.formatted(cpf, moeda, valor, agencia);
    }

        private OrdemCompra salvarOrdem(String moeda, LocalDateTime dataSolicitacao) {
                return ordemCompraRepository.save(new OrdemCompra(
                                1L,
                                "43488428095",
                                dataSolicitacao,
                                moeda,
                                new BigDecimal("50.00"),
                                new BigDecimal("5.200000"),
                                new BigDecimal("260.00"),
                                "7057"));
        }
}
