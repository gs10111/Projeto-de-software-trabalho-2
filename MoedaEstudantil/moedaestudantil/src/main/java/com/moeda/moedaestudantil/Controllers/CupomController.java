package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.DTO.MensagemResponseDTO;
import com.moeda.moedaestudantil.Enumerators.CupomStatus;
import com.moeda.moedaestudantil.Models.Cupom;
import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Models.Vantagem;
import com.moeda.moedaestudantil.Services.CupomService;
import com.moeda.moedaestudantil.Services.EstudanteService;
import com.moeda.moedaestudantil.Services.VantagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cupons")
public class CupomController {

    @Autowired
    private CupomService cupomService;

    @Autowired
    private EstudanteService estudanteService;

    @Autowired
    private VantagemService vantagemService;

    /**
     * Resgatar uma vantagem gerando um cupom
     */
    @PostMapping("/resgatar")
    public ResponseEntity<?> resgatarVantagem(
            @RequestParam Long estudanteId,
            @RequestParam Long vantagemId) {
        
        try {
            // Buscar estudante e vantagem
            Estudante estudante = estudanteService.buscarPorId(estudanteId);
            Vantagem vantagem = vantagemService.buscarPorId(vantagemId);
            
            // Resgatar vantagem (isso irá gerar um cupom)
            Cupom cupom = cupomService.gerarCupom(estudante, vantagem);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(cupom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensagemResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensagemResponseDTO("Erro ao resgatar vantagem: " + e.getMessage()));
        }
    }

    /**
     * Listar cupons do estudante
     */
    @GetMapping("/estudante/{estudanteId}")
    public ResponseEntity<Page<Cupom>> listarCuponsEstudante(
            @PathVariable Long estudanteId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) CupomStatus status) {
        
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("dataValidade").descending());
        
        Page<Cupom> cupons;
        if (status != null) {
            cupons = cupomService.buscarCuponsPorEstudanteEStatus(estudanteId, status, pageable);
        } else {
            cupons = cupomService.buscarCuponsPorEstudante(estudanteId, pageable);
        }
        
        return ResponseEntity.ok(cupons);
    }

    /**
     * Buscar cupom pelo código único
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> buscarCupomPorCodigo(@PathVariable String codigo) {
        try {
            Cupom cupom = cupomService.buscarPorCodigo(codigo);
            return ResponseEntity.ok(cupom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MensagemResponseDTO(e.getMessage()));
        }
    }

    /**
     * Atualizar status do cupom (usado pela empresa ao processar o cupom)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatusCupom(
            @PathVariable Long id,
            @RequestParam CupomStatus novoStatus) {
        
        try {
            Cupom cupomAtualizado = cupomService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(cupomAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensagemResponseDTO(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensagemResponseDTO("Erro ao atualizar status do cupom: " + e.getMessage()));
        }
    }

    /**
     * Listar cupons de uma empresa
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<Cupom>> listarCuponsEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) CupomStatus status) {
        
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("dataValidade").descending());
        
        Page<Cupom> cupons;
        if (status != null) {
            cupons = cupomService.buscarCuponsPorEmpresaEStatus(empresaId, status, pageable);
        } else {
            cupons = cupomService.buscarCuponsPorEmpresa(empresaId, pageable);
        }
        
        return ResponseEntity.ok(cupons);
    }
} 