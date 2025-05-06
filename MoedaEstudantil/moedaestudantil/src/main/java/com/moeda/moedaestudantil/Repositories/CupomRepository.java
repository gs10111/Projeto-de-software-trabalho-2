package com.moeda.moedaestudantil.Repositories;

import com.moeda.moedaestudantil.Enumerators.CupomStatus;
import com.moeda.moedaestudantil.Models.Cupom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {
    Optional<Cupom> findByCodigo(String codigo);
    List<Cupom> findByEstudanteId(Long estudanteId);
    List<Cupom> findByVantagemId(Long vantagemId);
    List<Cupom> findByEmpresaId(Long empresaId);
    List<Cupom> findByStatus(CupomStatus status);
    
    Page<Cupom> findByEstudanteId(Long estudanteId, Pageable pageable);
    
    Page<Cupom> findByEstudanteIdAndEmpresaId(Long estudanteId, Long empresaId, Pageable pageable);
    
    Page<Cupom> findByEstudanteIdAndStatus(Long estudanteId, CupomStatus status, Pageable pageable);
    
    Page<Cupom> findByEstudanteIdAndEmpresaIdAndStatus(Long estudanteId, Long empresaId, CupomStatus status, Pageable pageable);
    
    Page<Cupom> findByEmpresaId(Long empresaId, Pageable pageable);
    
    Page<Cupom> findByEmpresaIdAndStatus(Long empresaId, CupomStatus status, Pageable pageable);
    
    int countByEstudanteIdAndStatus(Long estudanteId, CupomStatus status);
    
    List<Cupom> findByDataValidadeBefore(LocalDateTime data);
    
    List<Cupom> findByDataValidadeBeforeAndStatus(LocalDateTime data, CupomStatus status);
    
    @Query("SELECT DISTINCT c.empresa.id FROM Cupom c WHERE c.estudante.id = :estudanteId")
    List<Long> findDistinctEmpresaIdsByEstudanteId(@Param("estudanteId") Long estudanteId);
}