package com.moeda.moedaestudantil.Models;

import com.moeda.moedaestudantil.Enumerators.CupomStatus;
import com.moeda.moedaestudantil.Enumerators.PerfilUsuario;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudante")
@PrimaryKeyJoinColumn(name = "usuario_id")
@DiscriminatorValue("ESTUDANTE")
public class Estudante extends Usuario {

    @Column(nullable = false)
    private String rg;

    @Column(nullable = false)
    private String curso;
    
    @Column(nullable = false)
    private int saldo = 0;

    @ManyToOne
    @JoinColumn(name = "instituicao_id", nullable = false)
    private Instituicao instituicao;
    
    @OneToMany(mappedBy = "estudante")
    private List<Transacao> transacoes = new ArrayList<>();
    
    @OneToMany(mappedBy = "estudante")
    private List<Cupom> cupons = new ArrayList<>();
    
    @Embedded
    private Endereco endereco;

    public Estudante() {
        super();
        this.setPerfil(PerfilUsuario.ALUNO);
    }

    public Estudante(String nome, String email, String senhaHash, String rg, String curso, Instituicao instituicao) {
        super(nome, email, senhaHash, PerfilUsuario.ALUNO);
        this.rg = rg;
        this.curso = curso;
        this.instituicao = instituicao;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
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

    public List<Cupom> getCupons() {
        return cupons;
    }
    
    public Endereco getEndereco() {
        return endereco;
    }
    
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<Transacao> consultarExtrato() {
        return this.transacoes;
    }

    public Cupom resgatarVantagem(Vantagem vantagem) {
        return null;
    }

    public void notificarMensagem(String mensagem) {
    }

    public Estudante orElseThrow(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
} 