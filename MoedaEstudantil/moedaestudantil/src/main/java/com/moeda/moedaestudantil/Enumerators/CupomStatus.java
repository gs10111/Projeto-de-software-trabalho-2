package com.moeda.moedaestudantil.Enumerators;

public enum CupomStatus {
    ATIVO("Ativo"),
    USADO("Usado"),
    EXPIRADO("Expirado"),
    CANCELADO("Cancelado"),
    PENDENTE("Pendente");
    
    private final String descricao;
    
    CupomStatus(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
} 