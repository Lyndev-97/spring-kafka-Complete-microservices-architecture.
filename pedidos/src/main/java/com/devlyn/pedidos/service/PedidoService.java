package com.devlyn.pedidos.service;

import com.devlyn.pedidos.client.ServicoBancarioClient;
import com.devlyn.pedidos.model.DadosPagamento;
import com.devlyn.pedidos.model.Pedido;
import com.devlyn.pedidos.model.enums.StatusPedido;
import com.devlyn.pedidos.model.enums.TipoPagamento;
import com.devlyn.pedidos.model.exception.ItemNaoEncontradoException;
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

    @Transactional
    public void adicionarNovoPagamento(Long codigoPedido, String dadosCartao, TipoPagamento tipo){

        var pedidoEncontrado = repository.findById(codigoPedido);
        if(pedidoEncontrado.isEmpty()){
            throw new ItemNaoEncontradoException("Pedido não encontrado para o código informado: " + codigoPedido);
        }

        var pedido = pedidoEncontrado.get();

        DadosPagamento dadosPagamento = new DadosPagamento();
        dadosPagamento.setTipoPagamento(tipo);
        dadosPagamento.setDados(dadosCartao);

        pedido.setDadosPagamento(dadosPagamento);
        pedido.setStatusPedido(StatusPedido.REALIZADO);
        pedido.setObservacoes("Um Novo pagamento foi enviado, aguardando o processamento");

        String novaChavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(novaChavePagamento);
        /**
         * repository.save(pedido); -> Nao é necessario salvar, devido a anotação @Transactional desse metodo.
         * Como o pedido foi obtido atraves do repository.findById(codigoPedido), qualquer alteracao por set
         * nesse pedido dentro desse metodo, sera capturada pela anotacao @Transactional que ira atualizar o
         * banco ao finalizar a execucao do metodo
         *
         */


    }
}
