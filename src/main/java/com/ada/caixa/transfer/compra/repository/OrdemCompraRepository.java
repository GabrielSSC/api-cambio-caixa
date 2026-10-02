package com.ada.caixa.transfer.compra.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ada.caixa.transfer.compra.domain.OrdemCompra;

@Repository
public interface OrdemCompraRepository extends JpaRepository<OrdemCompra, Long> {

	List<OrdemCompra> findAllByCpfClienteOrderByDataSolicitacaoDesc(String cpfCliente);
}
