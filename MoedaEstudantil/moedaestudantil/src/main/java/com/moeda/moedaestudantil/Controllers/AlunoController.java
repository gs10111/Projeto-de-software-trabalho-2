package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.Models.Aluno;
import com.moeda.moedaestudantil.Models.Instituicao;
import com.moeda.moedaestudantil.Repositories.AlunoRepository;
import com.moeda.moedaestudantil.Repositories.InstituicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cadastro/aluno")
public class AlunoController {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @GetMapping
    public String formCadastro(Model model) {
        model.addAttribute("aluno", new Aluno());
        model.addAttribute("instituicoes", instituicaoRepository.findAll());
        return "cadastro_aluno";
    }

    @PostMapping
    public String cadastrar(@ModelAttribute Aluno aluno) {
        alunoRepository.save(aluno);
        return "redirect:/login";
    }
}
