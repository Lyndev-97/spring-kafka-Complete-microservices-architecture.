package com.devlyn.pedidos.validator;

import com.devlyn.pedidos.client.ClientesClient;
import com.devlyn.pedidos.client.ProdutosClient;
import com.devlyn.pedidos.client.representation.ClienteRepresentation;
import com.devlyn.pedidos.client.representation.ProdutoRepresentation;
import com.devlyn.pedidos.model.ItemPedido;
import com.devlyn.pedidos.model.Pedido;
import com.devlyn.pedidos.model.exception.ValidationException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoValidator {

    private final ProdutosClient produtosClient;
    private final ClientesClient clientesClient;

    public void validar(Pedido pedido) {
            Long codigoCliente = pedido.getCodigoCliente();
            validarCliente(codigoCliente);
            pedido.getItens().forEach(this::validarItem);

            /*
        List<Long> codigosProdutos =
                pedido.getItens()
                        .stream().map(i -> i.getCodigoProduto()).toList();

        codigosProdutos.forEach(codigoProduto -> {
            ResponseEntity<ProdutoRepresentation> response = produtosClient.obterDados(codigoProduto);
            ProdutoRepresentation produto = response.getBody();
        });
        */

    }

    private void validarCliente(Long codigoCliente) {
        try {
            var response = clientesClient.obterDados(codigoCliente);
            //ResponseEntity<ClienteRepresentation> response = clientesClient.obterDados(codigoCliente);
            ClienteRepresentation cliente = response.getBody();
            log.info("Cliente de código {} encontrado: {}", cliente.codigo(), cliente.nome());
        }catch(FeignException.NotFound e) {
            var message = String.format("Cliente de código %d não encontrado",  codigoCliente);
            throw new ValidationException("codigoCliente", message);
        }
    }

    private void validarItem(ItemPedido item){
        try{
            var response = produtosClient.obterDados(item.getCodigoProduto());
            ProdutoRepresentation produto = response.getBody();
            log.info("Produto de código {} encontrado: {}", produto.codigo(), produto.nome());
        } catch (FeignException.NotFound e) {
            var message = String.format("Produto de código %d não encontrado",  item.getCodigoProduto());
            throw new ValidationException("codigoProduto", message);
        }
    }
}
