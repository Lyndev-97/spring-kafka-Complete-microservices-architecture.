package com.devlyn.pedidos.controller.dto;

import com.devlyn.pedidos.model.enums.TipoPagamento;

public record AdicaoNovoPagamentoDTO(
        Long codigoPedido, String dadosCartao, TipoPagamento tipoPagamento
) {}
