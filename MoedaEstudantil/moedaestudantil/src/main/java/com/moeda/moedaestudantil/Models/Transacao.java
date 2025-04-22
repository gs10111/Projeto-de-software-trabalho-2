package com.moeda.moedaestudantil.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transacao {
    @Id @GeneratedValue private Long id;

    private LocalDateTime data;

    private int quantidade;
    private String motivo;

    @ManyToOne
    private Professor professor;

    @ManyToOne
    private Aluno aluno;

    private boolean ehTroca; // false = doação, true = troca
    private String codigoTransacao;
}

