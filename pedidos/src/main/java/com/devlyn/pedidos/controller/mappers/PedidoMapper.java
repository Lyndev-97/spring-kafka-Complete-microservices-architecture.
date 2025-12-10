package com.devlyn.pedidos.controller.mappers;

import com.devlyn.pedidos.controller.dto.ItemPedidoDTO;
import com.devlyn.pedidos.controller.dto.NovoPedidoDTO;
import com.devlyn.pedidos.model.ItemPedido;
import com.devlyn.pedidos.model.Pedido;
import com.devlyn.pedidos.model.enums.StatusPedido;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    ItemPedidoMapper ITEM_PEDIDO_MAPPER = Mappers.getMapper(ItemPedidoMapper.class);

    @Mapping(source = "itens", target = "itens", qualifiedByName = "mapItens")
    @Mapping(source = "dadosPagamento", target = "dadosPagamento")
    Pedido map(NovoPedidoDTO dto);

    @Named("mapItens")
    default List<ItemPedido> mapItens(List<ItemPedidoDTO> dtos) {
            return dtos.stream().map(ITEM_PEDIDO_MAPPER::map).toList();
            /*
            ITEM_PEDIDO_MAPPER tem um metodo map que transforma ItemPedidoDTO em ItemPedido
            então quando voce faz um ITEM_PEDIDO_MAPPER::map é o mesmo que executar esse metodo
            para cada elemento da lista dtos que está na stream, e depois se usa o toList para
            converter em lista
             */
    }
    @AfterMapping
    default void afterMapping(@MappingTarget Pedido pedido){
        pedido.setStatusPedido(StatusPedido.REALIZADO);
        pedido.setDataPedido(LocalDateTime.now());

        var total = calcularTotal(pedido);
        pedido.setTotal(total);
        pedido.getItens().forEach(item -> item.setPedido(pedido));
    }

    private static BigDecimal calcularTotal(Pedido pedido){
        return pedido.getItens().stream().map(item ->
                item.getValorUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()))
                ).reduce(BigDecimal.ZERO, BigDecimal::add).abs();
    }
}
