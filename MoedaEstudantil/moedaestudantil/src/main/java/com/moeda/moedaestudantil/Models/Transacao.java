package com.moeda.moedaestudantil.Models;

import com.moeda.moedaestudantil.Enumerators.TransacaoTipo;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private int valor;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransacaoTipo tipo;

    @ManyToOne
    @JoinColumn(name = "emissor_id")
    private Professor emissor;

    @ManyToOne
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @Column(name = "vantagem_id")
    private Long vantagemId;

    public Transacao() {
        this.dataHora = LocalDateTime.now();
    }

    public Transacao(int valor, String descricao, TransacaoTipo tipo, Professor emissor, Estudante estudante) {
        this.valor = valor;
        this.descricao = descricao;
        this.tipo = tipo;
        this.emissor = emissor;
        this.estudante = estudante;
        this.dataHora = LocalDateTime.now();
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

    public Professor getEmissor() {
        return emissor;
    }

    public void setEmissor(Professor emissor) {
        this.emissor = emissor;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }

    public Long getVantagemId() {
        return vantagemId;
    }

    public void setVantagemId(Long vantagemId) {
        this.vantagemId = vantagemId;
    }
} 