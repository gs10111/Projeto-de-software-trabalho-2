package com.moeda.moedaestudantil.DTO;

public class MensagemResponseDTO {
    private String mensagem;

    public MensagemResponseDTO(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
} 