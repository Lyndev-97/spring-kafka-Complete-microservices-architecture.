package com.devlyn.pedidos.model;

public record ErrorResposta(
        String mensagem, String campo, String erro
) {
}
