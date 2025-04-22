package com.moeda.moedaestudantil.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.moeda.moedaestudantil.Models.Aluno;
import com.moeda.moedaestudantil.Models.Usuario;
import com.moeda.moedaestudantil.Repositories.UsuarioRepository;

@Controller
public class AlunoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/cadastro/aluno")
    public String showCadastroForm(Model model) {
        model.addAttribute("usuario", new Aluno());
        return "cadastro_aluno";
    }

    @PostMapping("/cadastro/aluno")
    public String submitCadastroForm(@ModelAttribute Usuario usuario) {
        usuarioRepository.save(usuario);  // Salva o usuário no banco de dados
        return "redirect:/cadastro/sucesso";
    }
}
