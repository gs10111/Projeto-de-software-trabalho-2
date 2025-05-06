package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.DTO.MensagemResponseDTO;
import com.moeda.moedaestudantil.DTO.TransacaoDTO;
import com.moeda.moedaestudantil.Enumerators.CupomStatus;
import com.moeda.moedaestudantil.Enumerators.TransacaoTipo;
import com.moeda.moedaestudantil.Models.Cupom;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Models.Transacao;
import com.moeda.moedaestudantil.Models.Vantagem;
import com.moeda.moedaestudantil.Services.CupomService;
import com.moeda.moedaestudantil.Services.EmpresaParceiraService;
import com.moeda.moedaestudantil.Services.EstudanteService;
import com.moeda.moedaestudantil.Services.TransacaoService;
import com.moeda.moedaestudantil.Services.VantagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estudantes")
public class EstudanteRESTController {

    @Autowired
    private EstudanteService estudanteService;

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private CupomService cupomService;

    @Autowired
    private VantagemService vantagemService;

    @Autowired
    private EmpresaParceiraService empresaService;

    /**
     * Buscar estudante por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarEstudante(@PathVariable Long id) {
        try {
            Estudante estudante = estudanteService.buscarPorId(id);
            return ResponseEntity.ok(estudante);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MensagemResponseDTO("Estudante não encontrado: " + e.getMessage()));
        }
    }

    /**
     * Consultar saldo do estudante
     */
    @GetMapping("/{id}/saldo")
    public ResponseEntity<Integer> consultarSaldo(@PathVariable Long id) {
        try {
            Estudante estudante = estudanteService.buscarPorId(id);
            int saldo = estudante.getSaldo();
            return ResponseEntity.ok(saldo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Consultar extrato do estudante
     */
    @GetMapping("/{id}/extrato")
    public ResponseEntity<List<TransacaoDTO>> consultarExtrato(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate dataInicio,
            @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) TransacaoTipo tipo) {
        
        try {
            Page<Transacao> transacoes = transacaoService.buscarTransacoesPorEstudante(
                    id, dataInicio, dataFim, tipo, Pageable.unpaged());
            
            List<TransacaoDTO> transacoesDTO = transacoes.getContent().stream()
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Listar cupons do estudante
     */
    @GetMapping("/{id}/cupons")
    public ResponseEntity<?> listarCupons(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) CupomStatus status) {
        
        try {
            Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("dataResgate").descending());
            
            Page<Cupom> cupons;
            if (status != null) {
                cupons = cupomService.buscarCuponsPorEstudanteEStatus(id, status, pageable);
            } else {
                cupons = cupomService.buscarCuponsPorEstudante(id, pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("cupons", cupons.getContent());
            response.put("paginaAtual", cupons.getNumber());
            response.put("totalItens", cupons.getTotalElements());
            response.put("totalPaginas", cupons.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MensagemResponseDTO("Erro ao buscar cupons: " + e.getMessage()));
        }
    }

    /**
     * Resgatar uma vantagem
     */
    @PostMapping("/{estudanteId}/resgatar")
    public ResponseEntity<?> resgatarVantagem(
            @PathVariable Long estudanteId,
            @RequestParam Long vantagemId) {
        
        try {
            // Buscar estudante e vantagem
            Estudante estudante = estudanteService.buscarPorId(estudanteId);
            Vantagem vantagem = vantagemService.buscarPorId(vantagemId);
            
            // Verificar saldo
            if (estudante.getSaldo() < vantagem.getValor()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new MensagemResponseDTO("Saldo insuficiente para resgatar esta vantagem"));
            }
            
            // Gerar cupom
            Cupom cupom = cupomService.gerarCupom(estudante, vantagem);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(cupom);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponseDTO("Erro ao resgatar vantagem: " + e.getMessage()));
        }
    }

    /**
     * Listar empresas parceiras com vantagens disponíveis
     */
    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaParceira>> listarEmpresasComVantagens() {
        List<EmpresaParceira> empresas = empresaService.listarEmpresasComVantagens();
        return ResponseEntity.ok(empresas);
    }

    /**
     * Listar vantagens disponíveis
     */
    @GetMapping("/vantagens")
    public ResponseEntity<Page<Vantagem>> listarVantagens(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) Integer valorMaximo,
            @RequestParam(required = false) String nome,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanho) {
        
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("valor").ascending());
        Page<Vantagem> vantagens = vantagemService.buscarVantagensDisponiveis(empresaId, valorMaximo, nome, pageable);
        
        return ResponseEntity.ok(vantagens);
    }
} 