package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.Models.Professor;
import com.moeda.moedaestudantil.Models.Transacao;
import com.moeda.moedaestudantil.Repositories.EstudanteRepository;
import com.moeda.moedaestudantil.Repositories.ProfessorRepository;
import com.moeda.moedaestudantil.Services.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private TransacaoService transacaoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Professor professor = getProfessorLogado();
        
        model.addAttribute("professor", professor);
        model.addAttribute("saldo", transacaoService.calcularSaldoProfessor(professor));
        model.addAttribute("transacoes", transacaoService.listarTransacoesPorProfessor(professor.getId()));
        
        return "professor/dashboard";
    }

    @GetMapping("/enviar-moedas")
    public String formEnviarMoedas(Model model) {
        Professor professor = getProfessorLogado();
        
        model.addAttribute("saldo", transacaoService.calcularSaldoProfessor(professor));
        model.addAttribute("estudantes", estudanteRepository.findByInstituicaoId(professor.getInstituicao().getId()));
        
        return "professor/enviar-moedas";
    }

    @PostMapping("/enviar-moedas")
    public String enviarMoedas(
            @RequestParam Long estudanteId,
            @RequestParam int quantidade,
            @RequestParam String motivo,
            RedirectAttributes redirectAttributes) {
        
        try {
            Professor professor = getProfessorLogado();
            
            Transacao transacao = transacaoService.criarTransacao(
                    professor.getId(),
                    estudanteId,
                    quantidade,
                    "Envio de moedas para reconhecimento",
                    motivo
            );
            
            redirectAttributes.addFlashAttribute("mensagem", "Moedas enviadas com sucesso!");
            return "redirect:/professor/dashboard";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/enviar-moedas";
        }
    }

    @GetMapping("/extrato")
    public String extrato(Model model) {
        Professor professor = getProfessorLogado();
        
        model.addAttribute("transacoes", transacaoService.listarTransacoesPorProfessor(professor.getId()));
        
        return "professor/extrato";
    }

    private Professor getProfessorLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return professorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Professor não encontrado"));
    }
} 