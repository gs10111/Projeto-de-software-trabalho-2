package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Enumerators.TransacaoTipo;
import com.moeda.moedaestudantil.Models.Professor;
import com.moeda.moedaestudantil.Repositories.ProfessorRepository;
import com.moeda.moedaestudantil.Repositories.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.List;

@Service
public class ProfessorService {

    private static final int MOEDAS_POR_SEMESTRE = 1000;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    public List<Professor> listarTodos() {
        return professorRepository.findAll();
    }

    public Professor buscarPorId(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado com ID: " + id));
    }

    public Professor salvar(Professor professor) {
        return professorRepository.save(professor);
    }

    /**
     * Calcula o saldo atual do professor, considerando as moedas recebidas por semestre
     * e as moedas já enviadas para estudantes
     */
    public int calcularSaldoProfessor(Long professorId) {
        Professor professor = buscarPorId(professorId);
        
        // Calcular quantidade de semestres desde o cadastro do professor ou início do sistema
        LocalDate dataAtual = LocalDate.now();
        int anoAtual = dataAtual.getYear();
        int mesAtual = dataAtual.getMonthValue();
        
        // Identificar o semestre atual
        boolean primeiroSemestre = mesAtual <= 6;
        
        // Total de moedas recebidas (considerando todos os semestres)
        int totalMoedasRecebidas = calcularTotalMoedasRecebidas(professor, anoAtual, primeiroSemestre);
        
        // Total de moedas enviadas para estudantes
        int totalMoedasEnviadas = calcularTotalMoedasEnviadas(professorId);
        
        return totalMoedasRecebidas - totalMoedasEnviadas;
    }
    
    /**
     * Calcula o total de moedas que o professor recebeu até o momento
     */
    private int calcularTotalMoedasRecebidas(Professor professor, int anoAtual, boolean primeiroSemestreAtual) {
        // Ano e semestre de entrada do professor
        int anoEntrada = professor.getDataCadastro().getYear();
        boolean primeiroSemestreEntrada = professor.getDataCadastro().getMonthValue() <= 6;
        
        int totalSemestres = 0;
        
        // Contar semestres completos entre o ano de entrada e o ano atual
        for (int ano = anoEntrada; ano <= anoAtual; ano++) {
            if (ano == anoEntrada && ano == anoAtual) {
                // Mesmo ano de entrada e ano atual
                if (primeiroSemestreEntrada && !primeiroSemestreAtual) {
                    totalSemestres += 2; // Dois semestres no mesmo ano
                } else if (!primeiroSemestreEntrada && !primeiroSemestreAtual) {
                    totalSemestres += 1; // Apenas o segundo semestre
                } else if (primeiroSemestreEntrada && primeiroSemestreAtual) {
                    totalSemestres += 1; // Apenas o primeiro semestre
                }
                // Se entrou no segundo semestre e ainda estamos no primeiro, não conta
            } else if (ano == anoEntrada) {
                // Ano de entrada
                totalSemestres += primeiroSemestreEntrada ? 2 : 1;
            } else if (ano == anoAtual) {
                // Ano atual
                totalSemestres += primeiroSemestreAtual ? 1 : 2;
            } else {
                // Anos completos entre entrada e atual
                totalSemestres += 2;
            }
        }
        
        return totalSemestres * MOEDAS_POR_SEMESTRE;
    }
    
    /**
     * Calcula o total de moedas que o professor já enviou para estudantes
     */
    private int calcularTotalMoedasEnviadas(Long professorId) {
        return transacaoRepository.sumValorByEmissorIdAndTipo(professorId, TransacaoTipo.RECEBIMENTO);
    }
    
    /**
     * Inicia um novo semestre, adicionando 1000 moedas ao saldo de todos os professores
     */
    @Transactional
    public void iniciarNovoSemestre() {
        // Este método seria chamado por um agendador no início de cada semestre
        // Não é necessário alterar o banco, pois o cálculo é feito dinamicamente
        // Mas poderia registrar a data de início do novo semestre para fins de auditoria
        System.out.println("Novo semestre iniciado: " + LocalDateTime.now());
    }
    
    /**
     * Método para validar se o professor tem saldo suficiente para enviar moedas
     */
    public boolean validarSaldo(Long professorId, int quantidade) {
        int saldoAtual = calcularSaldoProfessor(professorId);
        return saldoAtual >= quantidade;
    }
    
    /**
     * Método para criar um novo semestre acadêmico 
     * (Para fins de simulação/testes)
     */
    public void criarSemestre() {
        iniciarNovoSemestre();
    }
} 