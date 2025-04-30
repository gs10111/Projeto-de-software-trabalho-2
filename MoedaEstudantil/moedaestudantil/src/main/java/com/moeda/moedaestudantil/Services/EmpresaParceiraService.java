package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Repositories.CupomRepository;
import com.moeda.moedaestudantil.Repositories.EmpresaParceiraRepository;
import com.moeda.moedaestudantil.Repositories.VantagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaParceiraService {

    @Autowired
    private EmpresaParceiraRepository empresaRepository;
    
    @Autowired
    private VantagemRepository vantagemRepository;
    
    @Autowired
    private CupomRepository cupomRepository;
    
    public EmpresaParceira buscarPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Empresa não encontrada"));
    }
    
    public EmpresaParceira buscarPorEmail(String email) {
        return empresaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Empresa não encontrada"));
    }
    
    public List<EmpresaParceira> listarTodas() {
        return empresaRepository.findAll();
    }
    
    public List<EmpresaParceira> listarEmpresasComVantagens() {
        // Retornar apenas empresas que têm vantagens cadastradas
        return empresaRepository.findAll().stream()
                .filter(empresa -> !vantagemRepository.findByEmpresaIdAndDisponivelTrue(empresa.getId()).isEmpty())
                .collect(Collectors.toList());
    }
    
    public List<EmpresaParceira> listarEmpresasComCupons(Long estudanteId) {
        // Retornar empresas das quais o estudante possui cupons
        return empresaRepository.findAllByIdIn(
                cupomRepository.findDistinctEmpresaIdsByEstudanteId(estudanteId));
    }
} 