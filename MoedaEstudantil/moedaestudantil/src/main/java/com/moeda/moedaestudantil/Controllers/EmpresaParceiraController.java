package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.DTO.MensagemResponseDTO;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Vantagem;
import com.moeda.moedaestudantil.Services.EmpresaParceiraService;
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
@RequestMapping("/api/empresas")
public class EmpresaParceiraController {

    @Autowired
    private EmpresaParceiraService empresaService;

    @Autowired
    private VantagemService vantagemService;

    /**
     * Cadastrar nova empresa parceira
     */
    @PostMapping
    public ResponseEntity<?> cadastrarEmpresa(@RequestBody EmpresaParceira empresa) {
        try {
            EmpresaParceira empresaSalva = empresaService.salvar(empresa);
            return ResponseEntity.status(HttpStatus.CREATED).body(empresaSalva);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponseDTO("Erro ao cadastrar empresa: " + e.getMessage()));
        }
    }

    /**
     * Listar todas as empresas parceiras
     */
    @GetMapping
    public ResponseEntity<List<EmpresaParceira>> listarEmpresas() {
        List<EmpresaParceira> empresas = empresaService.listarTodas();
        return ResponseEntity.ok(empresas);
    }

    /**
     * Buscar empresa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarEmpresa(@PathVariable Long id) {
        try {
            EmpresaParceira empresa = empresaService.buscarPorId(id);
            return ResponseEntity.ok(empresa);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new MensagemResponseDTO("Empresa não encontrada: " + e.getMessage()));
        }
    }

    /**
     * Atualizar dados da empresa
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEmpresa(@PathVariable Long id, @RequestBody EmpresaParceira empresa) {
        try {
            // Verificar se a empresa existe
            EmpresaParceira empresaExistente = empresaService.buscarPorId(id);
            
            // Atualizar dados
            empresaExistente.setNome(empresa.getNome());
            empresaExistente.setEmail(empresa.getEmail());
            empresaExistente.setDescricao(empresa.getDescricao());
            
            // Salvar alterações
            EmpresaParceira empresaAtualizada = empresaService.salvar(empresaExistente);
            
            return ResponseEntity.ok(empresaAtualizada);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponseDTO("Erro ao atualizar empresa: " + e.getMessage()));
        }
    }

    /**
     * Cadastrar nova vantagem para a empresa
     */
    @PostMapping("/{empresaId}/vantagens")
    public ResponseEntity<?> cadastrarVantagem(@PathVariable Long empresaId, @RequestBody Vantagem vantagem) {
        try {
            // Buscar a empresa
            EmpresaParceira empresa = empresaService.buscarPorId(empresaId);
            
            // Associar a empresa à vantagem
            vantagem.setEmpresa(empresa);
            
            // Salvar a vantagem
            Vantagem vantagemSalva = vantagemService.salvar(vantagem);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(vantagemSalva);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponseDTO("Erro ao cadastrar vantagem: " + e.getMessage()));
        }
    }

    /**
     * Listar todas as vantagens de uma empresa
     */
    @GetMapping("/{empresaId}/vantagens")
    public ResponseEntity<Page<Vantagem>> listarVantagensPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("valor").ascending());
        Page<Vantagem> vantagens = vantagemService.buscarPorEmpresa(empresaId, pageable);
        
        return ResponseEntity.ok(vantagens);
    }

    /**
     * Remover uma vantagem
     */
    @DeleteMapping("/vantagens/{vantagemId}")
    public ResponseEntity<?> removerVantagem(@PathVariable Long vantagemId) {
        try {
            vantagemService.remover(vantagemId);
            return ResponseEntity.ok(new MensagemResponseDTO("Vantagem removida com sucesso"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponseDTO("Erro ao remover vantagem: " + e.getMessage()));
        }
    }

    /**
     * Atualizar status de uma vantagem (ativar/desativar)
     */
    @PutMapping("/vantagens/{vantagemId}/status")
    public ResponseEntity<?> atualizarStatusVantagem(
            @PathVariable Long vantagemId,
            @RequestParam boolean disponivel) {
        
        try {
            Vantagem vantagem = vantagemService.atualizarDisponibilidade(vantagemId, disponivel);
            return ResponseEntity.ok(vantagem);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MensagemResponseDTO("Erro ao atualizar status da vantagem: " + e.getMessage()));
        }
    }
} 