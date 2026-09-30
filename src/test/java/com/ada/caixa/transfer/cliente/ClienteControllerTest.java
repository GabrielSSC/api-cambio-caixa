package com.ada.caixa.transfer.cliente;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ada.caixa.transfer.cliente.controller.ClienteController;
import com.ada.caixa.transfer.cliente.domain.EstadoCivil;
import com.ada.caixa.transfer.cliente.domain.Sexo;
import com.ada.caixa.transfer.cliente.dto.ClienteRequestDTO;
import com.ada.caixa.transfer.cliente.dto.ClienteResponseDTO;
import com.ada.caixa.transfer.cliente.service.ClienteService;
import com.ada.caixa.transfer.config.SecurityConfig;
import com.ada.caixa.transfer.exception.ClienteNotFoundException;
import com.ada.caixa.transfer.exception.DuplicateCpfException;
import com.ada.caixa.transfer.web.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ClienteController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteService clienteService;

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @DisplayName("Deve retornar 201 Created quando cadastrar cliente com sucesso")
    void deveRetornar201AoCadastrarComSucesso() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Maria Souza",
                "43488428095",
                LocalDate.of(1990, 5, 15),
                EstadoCivil.SOLTEIRO,
                Sexo.FEMININO
        );

        ClienteResponseDTO response = new ClienteResponseDTO(
                1L,
                "Maria Souza",
                "43488428095",
                LocalDate.of(1990, 5, 15),
                EstadoCivil.SOLTEIRO,
                Sexo.FEMININO
        );

        when(clienteService.cadastrar(any(ClienteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id_cliente").value(1))
                .andExpect(jsonPath("$.nome").value("Maria Souza"))
                .andExpect(jsonPath("$.cpf").value("43488428095"));
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @DisplayName("Deve retornar 409 Conflict quando o CPF já estiver cadastrado")
    void deveRetornar409QuandoCpfJaExistir() throws Exception {
        ClienteRequestDTO request = new ClienteRequestDTO(
                "Maria Souza",
                "43488428095",
                LocalDate.of(1990, 5, 15),
                EstadoCivil.SOLTEIRO,
                Sexo.FEMININO
        );

        when(clienteService.cadastrar(any(ClienteRequestDTO.class)))
                .thenThrow(new DuplicateCpfException("CPF 43488428095 já está cadastrado."));

        mockMvc.perform(post("/api/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("CPF 43488428095 já está cadastrado."));
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @DisplayName("Deve retornar 400 Bad Request quando dados forem inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        // Envia campos vazios e CPF em formato incorreto
        String jsonInvalido = """
                {
                    "nome": "",
                    "cpf": "123",
                    "dataNascimento": "2099-01-01",
                    "estadoCivil": null,
                    "sexo": null
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @DisplayName("Deve retornar 200 e os dados do cliente quando o CPF existir")
    void deveRetornar200AoConsultarClienteExistente() throws Exception {
        ClienteResponseDTO response = new ClienteResponseDTO(
                1L,
                "Maria Souza",
                "43488428095",
                LocalDate.of(1990, 5, 15),
                EstadoCivil.SOLTEIRO,
                Sexo.FEMININO
        );

        when(clienteService.consultarPorCpf("43488428095")).thenReturn(response);

        mockMvc.perform(get("/api/clientes/43488428095"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_cliente").value(1))
                .andExpect(jsonPath("$.nome").value("Maria Souza"))
                .andExpect(jsonPath("$.cpf").value("43488428095"));
    }

    @Test
    @WithMockUser(username = "instructor", roles = {"INSTRUCTOR"})
    @DisplayName("Deve retornar 404 quando o CPF não estiver cadastrado")
    void deveRetornar404AoConsultarClienteInexistente() throws Exception {
        when(clienteService.consultarPorCpf("11111111111"))
                .thenThrow(new ClienteNotFoundException("Cliente com CPF 11111111111 não encontrado."));

        mockMvc.perform(get("/api/clientes/11111111111"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Cliente com CPF 11111111111 não encontrado."));
    }
}
