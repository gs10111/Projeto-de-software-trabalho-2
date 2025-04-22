package com.moeda.moedaestudantil.Repositories;

import com.moeda.moedaestudantil.Models.EmpresaParceira;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaParceiraRepository extends JpaRepository<EmpresaParceira, Long> {
    Optional<EmpresaParceira> findByEmail(String email);
}
