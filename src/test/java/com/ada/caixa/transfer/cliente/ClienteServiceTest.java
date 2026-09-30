package com.ada.caixa.transfer.cliente;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ada.caixa.transfer.cliente.domain.Cliente;
import com.ada.caixa.transfer.cliente.domain.EstadoCivil;
import com.ada.caixa.transfer.cliente.domain.Sexo;
import com.ada.caixa.transfer.cliente.dto.ClienteRequestDTO;
import com.ada.caixa.transfer.cliente.dto.ClienteResponseDTO;
import com.ada.caixa.transfer.cliente.repository.ClienteRepository;
import com.ada.caixa.transfer.cliente.service.ClienteService;
import com.ada.caixa.transfer.exception.ClienteNotFoundException;
import com.ada.caixa.transfer.exception.DuplicateCpfException;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    @DisplayName("Deve cadastrar cliente com sucesso quando CPF não existir")
    void deveCadastrarClienteComSucesso() {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Maria Souza",
                "43488428095",
                LocalDate.of(1990, 5, 15),
                EstadoCivil.SOLTEIRO,
                Sexo.FEMININO
        );

        when(clienteRepository.existsByCpf("43488428095")).thenReturn(false);

        Cliente clienteSalvo = new Cliente(
                dto.nome(),
                "43488428095",
                dto.dataNascimento(),
                dto.estadoCivil(),
                dto.sexo()
        );
        clienteSalvo.setId(1L);

        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteSalvo);

        ClienteResponseDTO resultado = clienteService.cadastrar(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.idCliente());
        assertEquals("Maria Souza", resultado.nome());
        assertEquals("43488428095", resultado.cpf());
        assertEquals(EstadoCivil.SOLTEIRO, resultado.estadoCivil());
        assertEquals(Sexo.FEMININO, resultado.sexo());

        verify(clienteRepository).existsByCpf("43488428095");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Deve lançar DuplicateCpfException quando CPF já estiver cadastrado")
    void deveLancarExcecaoQuandoCpfJaExistir() {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Maria Souza",
                "43488428095",
                LocalDate.of(1990, 5, 15),
                EstadoCivil.SOLTEIRO,
                Sexo.FEMININO
        );

        when(clienteRepository.existsByCpf("43488428095")).thenReturn(true);

        assertThrows(DuplicateCpfException.class, () -> clienteService.cadastrar(dto));

        verify(clienteRepository).existsByCpf("43488428095");
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar os dados do cliente encontrado pelo CPF")
    void deveConsultarClientePorCpf() {
        Cliente cliente = new Cliente(
                "Maria Souza",
                "43488428095",
                LocalDate.of(1990, 5, 15),
                EstadoCivil.SOLTEIRO,
                Sexo.FEMININO
        );
        cliente.setId(1L);
        when(clienteRepository.findByCpf("43488428095")).thenReturn(Optional.of(cliente));

        ClienteResponseDTO resultado = clienteService.consultarPorCpf("43488428095");

        assertEquals(1L, resultado.idCliente());
        assertEquals("Maria Souza", resultado.nome());
        verify(clienteRepository).findByCpf("43488428095");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o CPF não encontrar cliente")
    void deveLancarExcecaoAoConsultarCpfInexistente() {
        when(clienteRepository.findByCpf("11111111111")).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class,
                () -> clienteService.consultarPorCpf("11111111111"));
    }
}
