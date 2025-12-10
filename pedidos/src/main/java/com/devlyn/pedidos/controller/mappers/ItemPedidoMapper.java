package com.devlyn.pedidos.controller.mappers;

import com.devlyn.pedidos.controller.dto.ItemPedidoDTO;
import com.devlyn.pedidos.model.ItemPedido;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    ItemPedido map(ItemPedidoDTO dto);
}
