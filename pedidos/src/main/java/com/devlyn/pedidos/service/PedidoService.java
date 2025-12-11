package com.devlyn.pedidos.service;

import com.devlyn.pedidos.client.ServicoBancarioClient;
import com.devlyn.pedidos.model.Pedido;
import com.devlyn.pedidos.model.enums.StatusPedido;
import com.devlyn.pedidos.repository.ItemPedidoRepository;
import com.devlyn.pedidos.repository.PedidoRepository;
import com.devlyn.pedidos.validator.PedidoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository repository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator validator;
    private final ServicoBancarioClient servicoBancarioClient;

    @Transactional
    public Pedido criarPedido(Pedido pedido) {
        validator.validar(pedido);
        realizarPersistencia(pedido);
        enviarSolicitacaoPagamento(pedido);
        return pedido;
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        String chavePagemnto = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagemnto);
    }

    private void realizarPersistencia(Pedido pedido) {
        repository.save(pedido);
        itemPedidoRepository.saveAll( pedido.getItens() );
    }

    public void atualizarStatusPagamento(Long codigoPedido, String chavePagamento, boolean sucesso, String observacoes) {
        log.info("Buscando pedido com Código: {} e Chave: '{}'", codigoPedido, chavePagamento);
        var pedidoEncontrado = repository.findByCodigoAndChavePagamento(codigoPedido, chavePagamento);

        if(pedidoEncontrado.isEmpty()){
            log.error("Pedido não encontrado para o código : " + codigoPedido + " e chave pgmto: " + chavePagamento);
            return;
        }

        Pedido pedido = pedidoEncontrado.get();

        if(sucesso){
            pedido.setStatusPedido(StatusPedido.PAGO);
        } else{
            pedido.setStatusPedido(StatusPedido.ERRO_PAGAMENTO);
            pedido.setObservacoes(observacoes);
        }
            repository.save(pedido);
    }
}
