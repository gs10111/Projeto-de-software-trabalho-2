package com.moeda.moedaestudantil.Repositories;

import com.moeda.moedaestudantil.Models.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudanteRepository extends JpaRepository<Estudante, Long> {
    Optional<Estudante> findByEmail(String email);
    Optional<Estudante> findByRg(String rg);
    List<Estudante> findByInstituicaoId(Long instituicaoId);
    List<Estudante> findByCurso(String curso);
    List<Estudante> findByInstituicaoIdAndCurso(Long instituicaoId, String curso);
} 