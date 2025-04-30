package com.moeda.moedaestudantil.Models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instituicao")
public class Instituicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "instituicao")
    private List<Professor> professores = new ArrayList<>();

    @OneToMany(mappedBy = "instituicao")
    private List<Estudante> estudantes = new ArrayList<>();

    public Instituicao() {
    }

    public Instituicao(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public List<Estudante> getEstudantes() {
        return estudantes;
    }
} 