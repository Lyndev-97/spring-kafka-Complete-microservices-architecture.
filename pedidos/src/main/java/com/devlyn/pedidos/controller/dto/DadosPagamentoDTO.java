package com.devlyn.pedidos.controller.dto;

import com.devlyn.pedidos.model.enums.TipoPagamento;

public record DadosPagamentoDTO(String dados, TipoPagamento tipoPagamento) {
}
