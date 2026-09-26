package com.ada.caixa.transfer.cliente.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ada.caixa.transfer.cliente.domain.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /** Verifica se já existe algum cliente cadastrado com o CPF informado. */
    boolean existsByCpf(String cpf);

    /** Busca um cliente pelo seu CPF. */
    Optional<Cliente> findByCpf(String cpf);
}
