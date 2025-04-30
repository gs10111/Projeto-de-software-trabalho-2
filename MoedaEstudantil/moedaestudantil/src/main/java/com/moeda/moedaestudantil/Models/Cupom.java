package com.moeda.moedaestudantil.Models;

import com.moeda.moedaestudantil.Enumerators.CupomStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupom")
public class Cupom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(name = "data_resgate", nullable = false)
    private LocalDateTime dataResgate;

    @Column(name = "data_uso")
    private LocalDateTime dataUso;

    @Column(name = "data_validade")
    private LocalDateTime dataValidade;

    @Column(nullable = false)
    private int valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CupomStatus status;

    @ManyToOne
    @JoinColumn(name = "estudante_id", nullable = false)
    private Estudante estudante;

    @ManyToOne
    @JoinColumn(name = "vantagem_id", nullable = false)
    private Vantagem vantagem;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaParceira empresa;

    public Cupom() {
    }

    public Cupom(String codigo, Estudante estudante, Vantagem vantagem, EmpresaParceira empresa) {
        this.codigo = codigo;
        this.estudante = estudante;
        this.vantagem = vantagem;
        this.empresa = empresa;
        this.dataResgate = LocalDateTime.now();
        this.dataValidade = LocalDateTime.now().plusMonths(1); // Validade padrão de 1 mês
        this.status = CupomStatus.ATIVO;
        this.valor = vantagem.getValor();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getDataResgate() {
        return dataResgate;
    }

    public void setDataResgate(LocalDateTime dataResgate) {
        this.dataResgate = dataResgate;
    }

    public LocalDateTime getDataUso() {
        return dataUso;
    }

    public void setDataUso(LocalDateTime dataUso) {
        this.dataUso = dataUso;
    }

    public LocalDateTime getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDateTime dataValidade) {
        this.dataValidade = dataValidade;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public CupomStatus getStatus() {
        return status;
    }

    public void setStatus(CupomStatus status) {
        this.status = status;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }

    public Vantagem getVantagem() {
        return vantagem;
    }

    public void setVantagem(Vantagem vantagem) {
        this.vantagem = vantagem;
    }

    public EmpresaParceira getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaParceira empresa) {
        this.empresa = empresa;
    }

    public boolean isValid() {
        return this.status == CupomStatus.PENDENTE && this.dataResgate.isBefore(LocalDateTime.now()) && this.dataUso == null && LocalDateTime.now().isBefore(this.dataValidade);
    }

    public void notificarEmail() {
    }
} 