package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.Enumerators.PerfilUsuario;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Endereco;
import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Models.Instituicao;
import com.moeda.moedaestudantil.Repositories.EmpresaParceiraRepository;
import com.moeda.moedaestudantil.Repositories.EstudanteRepository;
import com.moeda.moedaestudantil.Repositories.InstituicaoRepository;
import com.moeda.moedaestudantil.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private EmpresaParceiraRepository empresaParceiraRepository;
    
    @Autowired
    private InstituicaoRepository instituicaoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/estudante")
    public String formCadastroEstudante(Model model) {
        model.addAttribute("estudante", new Estudante());
        model.addAttribute("endereco", new Endereco());
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "cadastro/estudante";
    }
    
    @PostMapping("/estudante")
    public String cadastrarEstudante(
            @ModelAttribute Estudante estudante,
            @ModelAttribute Endereco endereco,
            @RequestParam String senha,
            @RequestParam Long instituicaoId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar se o email já existe
            if (usuarioRepository.existsByEmail(estudante.getEmail())) {
                redirectAttributes.addFlashAttribute("erro", "Este email já está em uso");
                return "redirect:/cadastro/estudante";
            }
            
            // Buscar instituição
            Instituicao instituicao = instituicaoRepository.findById(instituicaoId)
                    .orElseThrow(() -> new IllegalArgumentException("Instituição não encontrada"));
            
            // Configurar estudante
            estudante.setSenhaHash(passwordEncoder.encode(senha));
            estudante.setPerfil(PerfilUsuario.ALUNO);
            estudante.setInstituicao(instituicao);
            estudante.setEndereco(endereco);
            
            // Salvar estudante
            estudanteRepository.save(estudante);
            
            redirectAttributes.addFlashAttribute("mensagem", "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao realizar cadastro: " + e.getMessage());
            return "redirect:/cadastro/estudante";
        }
    }
    
    @GetMapping("/empresa")
    public String formCadastroEmpresa(Model model) {
        model.addAttribute("empresa", new EmpresaParceira());
        return "cadastro/empresa";
    }
    
    @PostMapping("/empresa")
    public String cadastrarEmpresa(
            @ModelAttribute EmpresaParceira empresa,
            @RequestParam String senha,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar se a área de atuação é válida
            if (empresa.getAreaAtuacao() == null || empresa.getAreaAtuacao().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "A área de atuação é obrigatória");
                return "redirect:/cadastro/empresa";
            }
            
            // Verificar se o email já existe
            if (usuarioRepository.existsByEmail(empresa.getEmail())) {
                redirectAttributes.addFlashAttribute("erro", "Este email já está em uso");
                return "redirect:/cadastro/empresa";
            }
            
            // Configurar empresa
            empresa.setSenhaHash(passwordEncoder.encode(senha));
            empresa.setPerfil(PerfilUsuario.PARCEIRO);
            
            // Salvar empresa
            empresaParceiraRepository.save(empresa);
            
            redirectAttributes.addFlashAttribute("mensagem", "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/login";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao realizar cadastro: " + e.getMessage());
            return "redirect:/cadastro/empresa";
        }
    }

    public EmpresaParceira salvarEmpresa(EmpresaParceira empresa) {
        // Make sure areaAtuacao is set before saving
        if (empresa.getAreaAtuacao() == null) {
            throw new IllegalArgumentException("A área de atuação é obrigatória");
        }
        
        // Rest of the save logic
        return empresaParceiraRepository.save(empresa);
    }
}