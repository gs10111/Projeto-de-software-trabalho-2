package com.moeda.moedaestudantil.DTO;

import com.moeda.moedaestudantil.Enumerators.TransacaoTipo;

import java.time.LocalDateTime;

public class TransacaoDTO {
    private Long id;
    private LocalDateTime dataHora;
    private int valor;
    private String descricao;
    private TransacaoTipo tipo;
    private String emissorNome;
    private String estudanteNome;

    public TransacaoDTO() {
    }

    public TransacaoDTO(Long id, LocalDateTime dataHora, int valor, String descricao, 
                       TransacaoTipo tipo, String emissorNome, String estudanteNome) {
        this.id = id;
        this.dataHora = dataHora;
        this.valor = valor;
        this.descricao = descricao;
        this.tipo = tipo;
        this.emissorNome = emissorNome;
        this.estudanteNome = estudanteNome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TransacaoTipo getTipo() {
        return tipo;
    }

    public void setTipo(TransacaoTipo tipo) {
        this.tipo = tipo;
    }

    public String getEmissorNome() {
        return emissorNome;
    }

    public void setEmissorNome(String emissorNome) {
        this.emissorNome = emissorNome;
    }

    public String getEstudanteNome() {
        return estudanteNome;
    }

    public void setEstudanteNome(String estudanteNome) {
        this.estudanteNome = estudanteNome;
    }
} 