package com.moeda.moedaestudantil.Repositories;

import com.moeda.moedaestudantil.Models.EmpresaParceira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaParceiraRepository extends JpaRepository<EmpresaParceira, Long> {
    
    Optional<EmpresaParceira> findByEmail(String email);
    
    Optional<EmpresaParceira> findByCnpj(String cnpj);
    
    List<EmpresaParceira> findByAreaAtuacao(String areaAtuacao);
    
    List<EmpresaParceira> findAllByIdIn(List<Long> ids);
} 