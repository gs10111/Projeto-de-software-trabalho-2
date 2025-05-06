package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.DTO.MensagemResponseDTO;
import com.moeda.moedaestudantil.DTO.TransacaoDTO;
import com.moeda.moedaestudantil.Models.Professor;
import com.moeda.moedaestudantil.Models.Transacao;
import com.moeda.moedaestudantil.Services.ProfessorService;
import com.moeda.moedaestudantil.Services.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private TransacaoService transacaoService;

    /**
     * Buscar um professor pelo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Professor> buscarProfessor(@PathVariable Long id) {
        Professor professor = professorService.buscarPorId(id);
        return ResponseEntity.ok(professor);
    }

    /**
     * Listar todos os professores
     */
    @GetMapping
    public ResponseEntity<List<Professor>> listarProfessores() {
        List<Professor> professores = professorService.listarTodos();
        return ResponseEntity.ok(professores);
    }

    /**
     * Consultar saldo atual do professor
     */
    @GetMapping("/{id}/saldo")
    public ResponseEntity<Integer> consultarSaldo(@PathVariable Long id) {
        int saldo = professorService.calcularSaldoProfessor(id);
        return ResponseEntity.ok(saldo);
    }

    /**
     * Consultar extrato de transações do professor
     */
    @GetMapping("/{id}/extrato")
    public ResponseEntity<List<TransacaoDTO>> consultarExtrato(@PathVariable Long id) {
        List<Transacao> transacoes = transacaoService.listarTransacoesPorProfessor(id);
        
        List<TransacaoDTO> transacoesDTO = transacoes.stream()
            .map(transacao -> new TransacaoDTO(
                transacao.getId(),
                transacao.getDataHora(),
                transacao.getValor(),
                transacao.getDescricao(),
                transacao.getTipo(),
                transacao.getEmissor() != null ? transacao.getEmissor().getNome() : null,
                transacao.getEstudante() != null ? transacao.getEstudante().getNome() : null
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(transacoesDTO);
    }

    /**
     * Enviar moedas para um estudante
     */
    @PostMapping("/{professorId}/enviar-moedas")
    public ResponseEntity<?> enviarMoedas(
            @PathVariable Long professorId,
            @RequestParam Long estudanteId,
            @RequestParam int quantidade,
            @RequestParam String motivo) {
        
        try {
            // Validar saldo do professor
            if (!professorService.validarSaldo(professorId, quantidade)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponseDTO("Saldo insuficiente para realizar a transferência"));
            }
            
            // Criar a transação
            Transacao transacao = transacaoService.criarTransacao(
                    professorId,
                    estudanteId,
                    quantidade,
                    "Transferência de moedas",
                    motivo);
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TransacaoDTO(
                    transacao.getId(),
                    transacao.getDataHora(),
                    transacao.getValor(),
                    transacao.getDescricao(),
                    transacao.getTipo(),
                    transacao.getEmissor() != null ? transacao.getEmissor().getNome() : null,
                    transacao.getEstudante() != null ? transacao.getEstudante().getNome() : null
                ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensagemResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensagemResponseDTO("Erro ao enviar moedas: " + e.getMessage()));
        }
    }

    /**
     * Simular criação de novo semestre (para fins de teste)
     */
    @PostMapping("/novo-semestre")
    public ResponseEntity<MensagemResponseDTO> criarNovoSemestre() {
        professorService.iniciarNovoSemestre();
        return ResponseEntity.ok(new MensagemResponseDTO("Novo semestre iniciado com sucesso"));
    }
} 