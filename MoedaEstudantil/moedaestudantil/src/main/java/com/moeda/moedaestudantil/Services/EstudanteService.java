package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Repositories.EstudanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstudanteService {

    @Autowired
    private EstudanteRepository estudanteRepository;
    
    public Estudante buscarPorEmail(String email) {
        return estudanteRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Estudante não encontrado"));
    }
    
    public Estudante buscarPorId(Long id) {
        return estudanteRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Estudante não encontrado"));
    }
    
    public List<Estudante> listarPorInstituicao(Long instituicaoId) {
        return estudanteRepository.findByInstituicaoId(instituicaoId);
    }
    
    public void atualizarSaldo(Long estudanteId, int valor) {
        Estudante estudante = buscarPorId(estudanteId);
        estudante.setSaldo(estudante.getSaldo() + valor);
        estudanteRepository.save(estudante);
    }
    
    public boolean verificarSaldoSuficiente(Long estudanteId, int valorNecessario) {
        Estudante estudante = buscarPorId(estudanteId);
        return estudante.getSaldo() >= valorNecessario;
    }
} 