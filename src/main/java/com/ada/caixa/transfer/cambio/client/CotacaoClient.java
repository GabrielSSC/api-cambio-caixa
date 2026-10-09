package com.ada.caixa.transfer.cambio.client;

import com.ada.caixa.transfer.cambio.domain.Cotacao;

public interface CotacaoClient {

    Cotacao consultar(String moeda);
}
