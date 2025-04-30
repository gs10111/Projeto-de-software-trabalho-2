package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.Enumerators.CupomStatus;
import com.moeda.moedaestudantil.Models.Cupom;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Vantagem;
import com.moeda.moedaestudantil.Repositories.EmpresaParceiraRepository;
import com.moeda.moedaestudantil.Services.CupomService;
import com.moeda.moedaestudantil.Services.VantagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    @Autowired
    private EmpresaParceiraRepository empresaParceiraRepository;

    @Autowired
    private VantagemService vantagemService;

    @Autowired
    private CupomService cupomService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        EmpresaParceira empresa = getEmpresaLogada();
        
        model.addAttribute("empresa", empresa);
        model.addAttribute("vantagens", vantagemService.listarVantagensPorEmpresa(empresa.getId()));
        model.addAttribute("cupons", cupomService.listarCuponsPorEmpresa(empresa.getId()));
        model.addAttribute("cuponsRecentes", new ArrayList<>()); // Empty list for now
        
        return "empresa/dashboard";
    }

    @GetMapping("/vantagens")
    public String vantagens(Model model) {
        EmpresaParceira empresa = getEmpresaLogada();
        
        model.addAttribute("vantagens", vantagemService.listarVantagensPorEmpresa(empresa.getId()));
        
        return "empresa/vantagens";
    }

    @GetMapping("/vantagens/nova")
    public String formNovaVantagem(Model model) {
        model.addAttribute("vantagem", new Vantagem());
        return "empresa/form-vantagem";
    }

    @PostMapping("/vantagens/nova")
    public String salvarVantagem(@ModelAttribute Vantagem vantagem, RedirectAttributes redirectAttributes) {
        try {
            EmpresaParceira empresa = getEmpresaLogada();
            vantagemService.criarVantagem(vantagem, empresa.getId());
            
            redirectAttributes.addFlashAttribute("mensagem", "Vantagem cadastrada com sucesso!");
            return "redirect:/empresa/vantagens";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/empresa/vantagens/nova";
        }
    }

    @GetMapping("/vantagens/editar/{id}")
    public String formEditarVantagem(@PathVariable Long id, Model model) {
        EmpresaParceira empresa = getEmpresaLogada();
        Vantagem vantagem = vantagemService.buscarPorId(id);
        
        if (!vantagem.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/empresa/vantagens";
        }
        
        model.addAttribute("vantagem", vantagem);
        return "empresa/form-vantagem";
    }

    @PostMapping("/vantagens/editar/{id}")
    public String atualizarVantagem(@PathVariable Long id, @ModelAttribute Vantagem vantagem, RedirectAttributes redirectAttributes) {
        try {
            EmpresaParceira empresa = getEmpresaLogada();
            Vantagem vantagemExistente = vantagemService.buscarPorId(id);
            
            if (!vantagemExistente.getEmpresa().getId().equals(empresa.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para editar esta vantagem");
                return "redirect:/empresa/vantagens";
            }
            
            vantagemService.atualizarVantagem(id, vantagem);
            
            redirectAttributes.addFlashAttribute("mensagem", "Vantagem atualizada com sucesso!");
            return "redirect:/empresa/vantagens";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/empresa/vantagens/editar/" + id;
        }
    }

    @GetMapping("/cupons")
    public String cupons(Model model) {
        EmpresaParceira empresa = getEmpresaLogada();
        
        model.addAttribute("cupons", cupomService.listarCuponsPorEmpresa(empresa.getId()));
        
        return "empresa/cupons";
    }

    @GetMapping("/validar-cupom")
    public String formValidarCupom() {
        return "empresa/validar-cupom";
    }

    @PostMapping("/validar-cupom")
    public String validarCupom(@RequestParam("codigo") String codigo, RedirectAttributes redirectAttributes) {
        try {
            EmpresaParceira empresa = getEmpresaLogada();
            
            Cupom cupom = cupomService.buscarCupomPorCodigo(codigo)
                    .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));
            
            if (!cupom.getEmpresa().getId().equals(empresa.getId())) {
                redirectAttributes.addFlashAttribute("erro", "Este cupom não pertence à sua empresa");
                return "redirect:/empresa/validar-cupom";
            }
            
            if (cupom.getStatus() != CupomStatus.ATIVO) {
                redirectAttributes.addFlashAttribute("erro", 
                        "Este cupom já foi " + cupom.getStatus().getDescricao().toLowerCase());
                return "redirect:/empresa/validar-cupom";
            }
            
            Cupom cupomValidado = cupomService.atualizarStatusCupom(codigo, CupomStatus.USADO);
            
            redirectAttributes.addFlashAttribute("mensagem", 
                    "Cupom validado com sucesso! Vantagem: " + cupomValidado.getVantagem().getNome());
            redirectAttributes.addFlashAttribute("cupomValidado", cupomValidado);
            
            return "redirect:/empresa/validar-cupom";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/empresa/validar-cupom";
        }
    }

    private EmpresaParceira getEmpresaLogada() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        return empresaParceiraRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Empresa não encontrada"));
    }
}