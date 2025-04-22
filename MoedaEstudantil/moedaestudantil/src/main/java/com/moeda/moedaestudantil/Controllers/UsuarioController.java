package com.moeda.moedaestudantil.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.moeda.moedaestudantil.DTO.UsuarioForm;

import com.moeda.moedaestudantil.Models.Aluno;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Professor;
import com.moeda.moedaestudantil.Models.Usuario;
import com.moeda.moedaestudantil.Repositories.AlunoRepository;
import com.moeda.moedaestudantil.Repositories.EmpresaParceiraRepository;
import com.moeda.moedaestudantil.Repositories.ProfessorRepository;

@Controller
public class UsuarioController {

    @Autowired
    private AlunoRepository alunoRepo;
    @Autowired
    private ProfessorRepository professorRepo;
    @Autowired
    private EmpresaParceiraRepository EmpresaParceiraRepo;

    @GetMapping("/cadastro")
    public String exibirFormulario(Model model) {
        model.addAttribute("usuario", new UsuarioForm());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processarCadastro(@ModelAttribute("usuario") UsuarioForm form) {
        switch (form.getTipo()) {
            case "ALUNO":
                Aluno aluno = new Aluno();
                preencherUsuario(form, aluno);
                alunoRepo.save(aluno);
                break;
            case "PROFESSOR":
                Professor professor = new Professor();
                preencherUsuario(form, professor);
                professorRepo.save(professor);
                break;
            case "EMPRESA":
                EmpresaParceira empresa = new EmpresaParceira();
                preencherUsuario(form, empresa);
                EmpresaParceiraRepo.save(empresa);
                break;
        }
        return "redirect:/login";
    }

    private void preencherUsuario(UsuarioForm form, Usuario destino) {
        destino.setNome(form.getNome());
        destino.setEmail(form.getEmail());
        destino.setCpf(form.getCpf());
        destino.setRg(form.getRg());
        destino.setEndereco(form.getEndereco());
        destino.setCurso(form.getCurso());
        destino.setInstituicao(form.getInstituicao());
        destino.setSenha(form.getSenha());
    }

    private void copiarDados(Usuario origem, Usuario destino) {
        destino.setNome(origem.getNome());
        destino.setEmail(origem.getEmail());
        destino.setCpf(origem.getCpf());
        destino.setRg(origem.getRg());
        destino.setEndereco(origem.getEndereco());
        destino.setCurso(origem.getCurso());
        destino.setInstituicao(origem.getInstituicao());
        destino.setSenha(origem.getSenha());
    }
}

