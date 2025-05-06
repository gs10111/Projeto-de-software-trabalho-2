package com.moeda.moedaestudantil.Models;

import com.moeda.moedaestudantil.Enumerators.PerfilUsuario;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "empresa_parceira")
@PrimaryKeyJoinColumn(name = "usuario_id")
@DiscriminatorValue("EMPRESA")
public class EmpresaParceira extends Usuario {

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String areaAtuacao;

    @Column(length = 1000)
    private String descricao;

    private String website;

    @OneToMany(mappedBy = "empresa")
    private List<Vantagem> vantagens = new ArrayList<>();

    @OneToMany(mappedBy = "empresa")
    private List<Cupom> cupons = new ArrayList<>();

    @Embedded
    private Endereco endereco;

    public EmpresaParceira() {
        super();
        this.setPerfil(PerfilUsuario.PARCEIRO);
    }

    public EmpresaParceira(String nome, String email, String senhaHash, String cnpj, String telefone, String areaAtuacao) {
        super(nome, email, senhaHash, PerfilUsuario.PARCEIRO);
        this.cnpj = cnpj;
        this.telefone = telefone;
        this.areaAtuacao = areaAtuacao;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {  // Match parameter name to field name
        this.telefone = telefone;
    }
    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public void cadastrarVantagem(Vantagem vantagem) {
        vantagem.setEmpresa(this);
        this.vantagens.add(vantagem);
    }

    public void notificarResgate(Cupom cupom) {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}