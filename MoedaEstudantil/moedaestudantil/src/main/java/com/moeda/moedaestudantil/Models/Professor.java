package com.moeda.moedaestudantil.Models;

import com.moeda.moedaestudantil.Enumerators.PerfilUsuario;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professor")
@PrimaryKeyJoinColumn(name = "usuario_id")
@DiscriminatorValue("PROFESSOR")
public class Professor extends Usuario {

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String departamento;

    @ManyToOne
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;

    @OneToMany(mappedBy = "emissor")
    private List<Transacao> transacoes = new ArrayList<>();

    public Professor() {
        super();
        this.setPerfil(PerfilUsuario.PROFESSOR);
    }

    public Professor(String nome, String email, String senhaHash, String cpf, String departamento, Instituicao instituicao) {
        super(nome, email, senhaHash, PerfilUsuario.PROFESSOR);
        this.cpf = cpf;
        this.departamento = departamento;
        this.instituicao = instituicao;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public Instituicao getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(Instituicao instituicao) {
        this.instituicao = instituicao;
    }

    public List<Transacao> getTransacoes() {
        return transacoes;
    }

    public void enviarMoedas(Estudante estudante, int quantidade, String motivo) {
    }

    public List<Transacao> consultarExtrato() {
        return this.transacoes;
    }

    public void criarSemestre() {
    }
} 