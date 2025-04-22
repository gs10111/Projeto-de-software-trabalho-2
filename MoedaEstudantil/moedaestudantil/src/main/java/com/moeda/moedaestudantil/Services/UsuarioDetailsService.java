package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Models.Aluno;
import com.moeda.moedaestudantil.Models.Professor;
import com.moeda.moedaestudantil.Models.UsuarioDetails;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Repositories.AlunoRepository;
import com.moeda.moedaestudantil.Repositories.EmpresaParceiraRepository;
import com.moeda.moedaestudantil.Repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final AlunoRepository alunoRepo;
    private final ProfessorRepository professorRepo;
    private final EmpresaParceiraRepository empresaRepo;

    // Construtor manualmente definido
    @Autowired
    public UsuarioDetailsService(AlunoRepository alunoRepo, ProfessorRepository professorRepo, EmpresaParceiraRepository empresaRepo) {
        this.alunoRepo = alunoRepo;
        this.professorRepo = professorRepo;
        this.empresaRepo = empresaRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return alunoRepo.findByEmail(email)
                .map(aluno -> new UsuarioDetails(aluno.getEmail(), aluno.getSenha(), "ALUNO"))
            .or(() -> professorRepo.findByEmail(email)
                .map(prof -> new UsuarioDetails(prof.getEmail(), prof.getSenha(), "PROFESSOR")) )
            .or(() -> empresaRepo.findByEmail(email)
                .map(emp -> new UsuarioDetails(emp.getEmail(), emp.getSenha(), "EMPRESA")))
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
}
