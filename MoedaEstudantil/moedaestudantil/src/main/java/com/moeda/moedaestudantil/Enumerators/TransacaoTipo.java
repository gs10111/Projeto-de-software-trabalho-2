package com.moeda.moedaestudantil.Enumerators;

public enum TransacaoTipo {
    RECEBIMENTO("Recebimento"),
    RESGATE("Resgate");
    
    private final String descricao;
    
    TransacaoTipo(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
} 