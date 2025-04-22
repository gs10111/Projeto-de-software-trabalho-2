package com.moeda.moedaestudantil.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.moeda.moedaestudantil.DTO.UsuarioForm;
import com.moeda.moedaestudantil.Models.Aluno;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Professor;
import com.moeda.moedaestudantil.Models.Usuario;
import com.moeda.moedaestudantil.Repositories.AlunoRepository;
import com.moeda.moedaestudantil.Repositories.EmpresaParceiraRepository;
import com.moeda.moedaestudantil.Repositories.ProfessorRepository;
import com.moeda.moedaestudantil.Repositories.UsuarioRepository;

@Controller
public class UsuarioController {

    @Autowired
    private AlunoRepository alunoRepo;
    @Autowired
    private ProfessorRepository professorRepo;
    @Autowired
    private EmpresaParceiraRepository EmpresaParceiraRepo;
    @Autowired
    private UsuarioRepository usuarioRepository;

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

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    @PostMapping("/usuarios/editar/{id}")
    public String atualizarUsuario(@PathVariable Long id, @ModelAttribute("usuario") Usuario usuarioForm) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));

        switch (usuarioForm) {
            case Aluno alunoForm -> {
            copiarDados(alunoForm, (Aluno) usuarioExistente);
            alunoRepo.save((Aluno) usuarioExistente);
            }
            case Professor professorForm -> {
            copiarDados(professorForm, (Professor) usuarioExistente);
            professorRepo.save((Professor) usuarioExistente);
            }
            case EmpresaParceira empresaForm -> {
            copiarDados(empresaForm, (EmpresaParceira) usuarioExistente);
            EmpresaParceiraRepo.save((EmpresaParceira) usuarioExistente);
            }
            default -> throw new IllegalArgumentException("Tipo de usuário desconhecido");
        }
            EmpresaParceira empresaForm = (EmpresaParceira) usuarioForm;
            copiarDados(empresaForm, (EmpresaParceira) usuarioExistente);
            EmpresaParceiraRepo.save((EmpresaParceira) usuarioExistente);

        return "redirect:/usuarios"; 
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + id));
        
        if (usuario instanceof Aluno) {
            Aluno aluno = (Aluno) usuario;
            model.addAttribute("usuario", aluno);
        } else if (usuario instanceof Professor) {
            Professor professor = (Professor) usuario;
            model.addAttribute("usuario", professor); 
        } else if (usuario instanceof EmpresaParceira) {
            EmpresaParceira empresa = (EmpresaParceira) usuario;
            model.addAttribute("usuario", empresa); 
        }

        return "usuarios/editar";
    }

    @GetMapping("/usuarios/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuarios";
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

