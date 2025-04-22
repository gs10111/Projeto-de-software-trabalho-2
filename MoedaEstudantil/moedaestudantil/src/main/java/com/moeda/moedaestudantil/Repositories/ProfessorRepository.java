package com.moeda.moedaestudantil.Repositories;

import com.moeda.moedaestudantil.Models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByEmail(String email);
}
