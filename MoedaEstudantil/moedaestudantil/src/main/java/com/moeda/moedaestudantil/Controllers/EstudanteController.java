package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.Enumerators.CupomStatus;
import com.moeda.moedaestudantil.Models.Cupom;
import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Repositories.CupomRepository;
import com.moeda.moedaestudantil.Services.EstudanteService;
import com.moeda.moedaestudantil.Services.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/estudante")
public class EstudanteController {

    @Autowired
    private EstudanteService estudanteService;

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private CupomRepository cupomRepository;

    // === VIEW THYMELEAF ===
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        Estudante est = estudanteService.buscarPorEmail(email);
        if (est == null) {
            throw new IllegalStateException("Estudante não encontrado para o e-mail: " + email);
        }

        model.addAttribute("estudante",    est);
        model.addAttribute("saldo",         transacaoService.calcularSaldoEstudante(est));
        model.addAttribute("transacoes",    transacaoService.listarTransacoesPorEstudante(est.getId()));
        model.addAttribute("cupons", cupomRepository.findByEstudanteId(est.getId(), Pageable.unpaged()).getContent());

        return "estudante/dashboard";
    }

    // === API JSON ===
    @GetMapping(
      value    = "/{id}/cupons",
      produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> meusCupons(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) CupomStatus status) {

        Estudante est = estudanteService.buscarPorId(id);
        if (est == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Estudante não encontrado com id " + id));
        }

        Pageable paging = PageRequest.of(page, size);
        Page<Cupom> cupons = (empresaId != null && status != null)
            ? cupomRepository.findByEstudanteIdAndEmpresaIdAndStatus(id, empresaId, status, paging)
            : (empresaId != null)
                ? cupomRepository.findByEstudanteIdAndEmpresaId(id, empresaId, paging)
                : (status != null)
                    ? cupomRepository.findByEstudanteIdAndStatus(id, status, paging)
                    : cupomRepository.findByEstudanteId(id, paging);

        List<Long> empresasIds = cupomRepository.findDistinctEmpresaIdsByEstudanteId(id);

        Map<String, Object> resp = new HashMap<>();
        resp.put("cupons",        cupons.getContent());
        resp.put("currentPage",   cupons.getNumber());
        resp.put("totalItems",    cupons.getTotalElements());
        resp.put("totalPages",    cupons.getTotalPages());
        resp.put("empresasIds",   empresasIds);

        return ResponseEntity.ok(resp);
    }
}
