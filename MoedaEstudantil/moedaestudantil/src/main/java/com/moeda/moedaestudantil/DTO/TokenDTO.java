package com.moeda.moedaestudantil.DTO;

import com.moeda.moedaestudantil.Enumerators.PerfilUsuario;

public class TokenDTO {
    private String token;
    private Long userId;
    private String nome;
    private PerfilUsuario perfil;

    public TokenDTO() {
    }

    public TokenDTO(String token, Long userId, String nome, PerfilUsuario perfil) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
        this.perfil = perfil;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
} 