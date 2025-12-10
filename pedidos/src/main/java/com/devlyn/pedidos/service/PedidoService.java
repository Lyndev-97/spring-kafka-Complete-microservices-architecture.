package com.devlyn.pedidos.service;

import com.devlyn.pedidos.model.Pedido;
import com.devlyn.pedidos.repository.ItemPedidoRepository;
import com.devlyn.pedidos.repository.PedidoRepository;
import com.devlyn.pedidos.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator pedidoValidator;

    public Pedido criarPedido(Pedido pedido) {
        repository.save( pedido );
        itemPedidoRepository.saveAll( pedido.getItens() );
        return pedido;
    }
}
