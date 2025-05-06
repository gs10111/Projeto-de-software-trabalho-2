package com.moeda.moedaestudantil.config;

import com.moeda.moedaestudantil.Enumerators.PerfilUsuario;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Models.Instituicao;
import com.moeda.moedaestudantil.Models.Professor;
import com.moeda.moedaestudantil.Repositories.EmpresaParceiraRepository;
import com.moeda.moedaestudantil.Repositories.EstudanteRepository;
import com.moeda.moedaestudantil.Repositories.InstituicaoRepository;
import com.moeda.moedaestudantil.Repositories.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("!prod") // Não executar em produção
    public CommandLineRunner initData(
            @Autowired InstituicaoRepository instituicaoRepository,
            @Autowired EstudanteRepository estudanteRepository,
            @Autowired ProfessorRepository professorRepository,
            @Autowired EmpresaParceiraRepository empresaRepository,
            @Autowired BCryptPasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Verificar se já existem dados
            if (estudanteRepository.count() > 0) {
                System.out.println("Dados já existem, pulando inicialização");
                return;
            }

            System.out.println("Inicializando dados para teste...");

            // Criar instituição
            Instituicao instituicao = new Instituicao();
            instituicao.setNome("Universidade Teste");
            instituicaoRepository.save(instituicao);

            // Criar estudante
            Estudante estudante = new Estudante();
            estudante.setNome("Estudante Teste");
            estudante.setEmail("estudante@teste.com");
            estudante.setRg("MG-12345678");
            estudante.setCurso("Ciência da Computação");
            estudante.setSaldo(50);
            estudante.setPerfil(PerfilUsuario.ALUNO);
            estudante.setSenhaHash(passwordEncoder.encode("123456"));
            estudante.setInstituicao(instituicao);
            estudanteRepository.save(estudante);

            // Criar professor
            Professor professor = new Professor();
            professor.setNome("Professor Teste");
            professor.setEmail("professor@teste.com");
            professor.setDepartamento("Departamento de Computação");
            professor.setCpf("123.456.789-00");
            professor.setPerfil(PerfilUsuario.PROFESSOR);
            professor.setSenhaHash(passwordEncoder.encode("123456"));
            professor.setInstituicao(instituicao);
            professorRepository.save(professor);

            // Criar empresa parceira
            EmpresaParceira empresa = new EmpresaParceira();
            empresa.setNome("Empresa Teste");
            empresa.setEmail("empresa@teste.com");
            empresa.setDescricao("Empresa para testes do sistema");
            empresa.setAreaAtuacao("Tecnologia");
            empresa.setCnpj("12.345.678/0001-90");
            empresa.setTelefone("(31) 3333-4444");
            empresa.setPerfil(PerfilUsuario.PARCEIRO);
            empresa.setSenhaHash(passwordEncoder.encode("123456"));
            empresaRepository.save(empresa);

            System.out.println("Dados de teste inicializados com sucesso!");
        };
    }
} 