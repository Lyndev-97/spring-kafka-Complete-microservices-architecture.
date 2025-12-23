package com.devlyn.pedidos.controller;

import com.devlyn.pedidos.controller.dto.AdicaoNovoPagamentoDTO;
import com.devlyn.pedidos.controller.dto.NovoPedidoDTO;
import com.devlyn.pedidos.controller.mappers.PedidoMapper;
import com.devlyn.pedidos.model.ErrorResposta;
import com.devlyn.pedidos.model.exception.ItemNaoEncontradoException;
import com.devlyn.pedidos.model.exception.ValidationException;
import com.devlyn.pedidos.service.PedidoService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService service;
    private final PedidoMapper mapper;

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody NovoPedidoDTO novoPedidoDto) {
        try {
            var pedido = mapper.map(novoPedidoDto);
            var novoPedido = service.criarPedido(pedido);
            return ResponseEntity.ok(novoPedido.getCodigo());
        }catch (ValidationException ex) {
            var erro = new ErrorResposta("Erro de validação dos campos", ex.getField(), ex.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @PostMapping("pagamentos")
    public ResponseEntity<Object> adicionarNovoPagamento(@RequestBody AdicaoNovoPagamentoDTO dto) {
        try {
            service.adicionarNovoPagamento(dto.codigoPedido(), dto.dadosCartao(), dto.tipoPagamento());
            return ResponseEntity.noContent().build();
        }catch(ItemNaoEncontradoException e){
            var erro = new ErrorResposta("Item não encontrado", "codigoPedido", e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }
}
