package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Enumerators.TransacaoTipo;
import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Models.Professor;
import com.moeda.moedaestudantil.Models.Transacao;
import com.moeda.moedaestudantil.Repositories.EstudanteRepository;
import com.moeda.moedaestudantil.Repositories.ProfessorRepository;
import com.moeda.moedaestudantil.Repositories.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EstudanteService estudanteService;

    public List<Transacao> listarTransacoesPorProfessor(Long professorId) {
        return transacaoRepository.findByEmissorId(professorId);
    }

    public List<Transacao> listarTransacoesPorEstudante(Long estudanteId) {
        return transacaoRepository.findByEstudanteId(estudanteId, Pageable.unpaged()).getContent();
    }

    @Transactional
    public Transacao criarTransacao(Long professorId, Long estudanteId, int quantidade, String descricao, String motivo) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));

        Estudante estudante = estudanteRepository.findById(estudanteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudante não encontrado"));

        // Verificação do saldo do professor
        int saldoProfessor = this.calcularSaldoProfessor(professor);
        if (saldoProfessor < quantidade) {
            throw new IllegalArgumentException("Saldo insuficiente para realizar transferência");
        }

        Transacao transacao = new Transacao();
        transacao.setDataHora(LocalDateTime.now());
        transacao.setValor(quantidade);
        transacao.setDescricao(descricao);
        transacao.setTipo(TransacaoTipo.RECEBIMENTO);
        transacao.setEmissor(professor);
        transacao.setEstudante(estudante);
        
        Transacao transacaoSalva = transacaoRepository.save(transacao);
        
        // Enviar notificação por email
        emailService.enviarEmailTransacao(transacaoSalva);
        
        return transacaoSalva;
    }

    @Transactional
    public Transacao registrarTransacao(Long professorId, Long estudanteId, int valor, 
                                       TransacaoTipo tipo, String descricao, Long vantagemId) {
        Transacao transacao = new Transacao();
        transacao.setValor(valor);
        transacao.setTipo(tipo);
        transacao.setDescricao(descricao);
        transacao.setDataHora(LocalDateTime.now());
        
        // Configurar professor (se aplicável)
        if (professorId != null) {
            Professor professor = new Professor();
            professor.setId(professorId);
            transacao.setEmissor(professor);
        }
        
        // Configurar estudante
        Estudante estudante = estudanteService.buscarPorId(estudanteId);
        transacao.setEstudante(estudante);
        
        return transacaoRepository.save(transacao);
    }
    
    public Page<Transacao> buscarTransacoesPorEstudante(Long estudanteId, LocalDate dataInicio, 
                                                       LocalDate dataFim, TransacaoTipo tipo, Pageable pageable) {
        LocalDateTime inicio = null;
        LocalDateTime fim = null;
        
        if (dataInicio != null) {
            inicio = dataInicio.atStartOfDay();
        }
        
        if (dataFim != null) {
            fim = dataFim.plusDays(1).atStartOfDay();
        }
        
        if (inicio != null && fim != null && tipo != null) {
            return transacaoRepository.findByEstudanteIdAndDataHoraBetweenAndTipo(
                    estudanteId, inicio, fim, tipo, pageable);
        } else if (inicio != null && fim != null) {
            return transacaoRepository.findByEstudanteIdAndDataHoraBetween(
                    estudanteId, inicio, fim, pageable);
        } else if (tipo != null) {
            return transacaoRepository.findByEstudanteIdAndTipo(estudanteId, tipo, pageable);
        } else {
            return transacaoRepository.findByEstudanteId(estudanteId, pageable);
        }
    }
    
    public List<Transacao> listarUltimasTransacoesPorEstudante(Long estudanteId, int quantidade) {
        Pageable pageable = Pageable.ofSize(quantidade);
        return transacaoRepository.findByEstudanteIdOrderByDataHoraDesc(estudanteId, pageable);
    }
    
    public int calcularTotalRecebidoEstudante(Long estudanteId) {
        return transacaoRepository.sumValorByEstudanteIdAndTipo(estudanteId, TransacaoTipo.RECEBIMENTO);
    }
    
    public int calcularTotalResgatadoEstudante(Long estudanteId) {
        return transacaoRepository.sumValorByEstudanteIdAndTipo(estudanteId, TransacaoTipo.RESGATE);
    }
    
    public int calcularSaldoProfessor(Professor professor) {
        // Lógica para calcular o saldo do professor
        // Note: Depende da regra de negócio específica sobre como o saldo de professor é calculado
        return 0;
    }
    
    public int calcularSaldoEstudante(Estudante estudante) {
        // Retornar o saldo armazenado no objeto estudante, que já deve estar atualizado pelo sistema
        return estudante.getSaldo();
    }
    
    public void validarSaldoContaProfessor(Long professorId, int quantidade) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));
        
        int saldo = calcularSaldoProfessor(professor);
        if (saldo < quantidade) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
    }
} 