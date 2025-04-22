package com.moeda.moedaestudantil.Controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.moeda.moedaestudantil.Enumerators.Role;
import com.moeda.moedaestudantil.Models.Usuario;
import com.moeda.moedaestudantil.Repositories.UsuarioRepository;

@Controller
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/cadastro")
    public String escolhaCadastro() {
        return "cadastro"; // Página com opções de tipo (aluno, professor, empresa)
    }

    @GetMapping("/cadastro/{tipo}")
    public String cadastroPorTipo(@PathVariable String tipo, Model model) {
        System.out.println("Tipo de cadastro: " + tipo); 
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        model.addAttribute("tipo", tipo); 

        switch (tipo) {
            case "aluno": return "cadastro_aluno";
            case "professor": return "cadastro_professor";
            case "empresa": return "cadastro_empresa";
            default: return "redirect:/cadastro";
        }
    }

    @PostMapping("/cadastro/{tipo}")
    public String processarCadastro(@PathVariable String tipo, @ModelAttribute Usuario usuario) {
        switch (tipo) {
            case "aluno":
                usuario.setRole(Role.ALUNO);
                break;
            case "professor":
                usuario.setRole(Role.PROFESSOR);
                break;
            case "empresa":
                usuario.setRole(Role.EMPRESA);
                break;
            default:
                return "redirect:/cadastro";
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }
}
