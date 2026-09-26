package com.ada.caixa.transfer.cliente.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ada.caixa.transfer.cliente.domain.Cliente;
import com.ada.caixa.transfer.cliente.dto.ClienteRequestDTO;
import com.ada.caixa.transfer.cliente.dto.ClienteResponseDTO;
import com.ada.caixa.transfer.cliente.repository.ClienteRepository;
import com.ada.caixa.transfer.exception.DuplicateCpfException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * UC1 · Cadastrar cliente
     *
     * Pré-condição: CPF ainda não cadastrado na base.
     * Se já existir, lança DuplicateCpfException (tratada como 409 Conflict).
     */
    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        String cpfLimpo = dto.getCpfLimpo();

        if (clienteRepository.existsByCpf(cpfLimpo)) {
            throw new DuplicateCpfException("CPF " + cpfLimpo + " já está cadastrado.");
        }

        Cliente novoCliente = new Cliente(
            dto.nome(),
            cpfLimpo,
            dto.dataNascimento(),
            dto.estadoCivil(),
            dto.sexo()
        );

        Cliente clienteSalvo = clienteRepository.save(novoCliente);

        return ClienteResponseDTO.fromEntity(clienteSalvo);
    }
}
