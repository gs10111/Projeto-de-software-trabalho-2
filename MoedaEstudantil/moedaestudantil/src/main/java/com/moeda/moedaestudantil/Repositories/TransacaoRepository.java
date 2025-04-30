package com.moeda.moedaestudantil.Repositories;

import com.moeda.moedaestudantil.Enumerators.TransacaoTipo;
import com.moeda.moedaestudantil.Models.Transacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    
    List<Transacao> findByEmissorId(Long emissorId);
    
    Page<Transacao> findByEstudanteId(Long estudanteId, Pageable pageable);
    
    Page<Transacao> findByEstudanteIdAndTipo(Long estudanteId, TransacaoTipo tipo, Pageable pageable);
    
    Page<Transacao> findByEstudanteIdAndDataHoraBetween(Long estudanteId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
    
    Page<Transacao> findByEstudanteIdAndDataHoraBetweenAndTipo(
            Long estudanteId, LocalDateTime inicio, LocalDateTime fim, TransacaoTipo tipo, Pageable pageable);
    
    List<Transacao> findByEstudanteIdOrderByDataHoraDesc(Long estudanteId, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transacao t WHERE t.estudante.id = :estudanteId AND t.tipo = :tipo")
    int sumValorByEstudanteIdAndTipo(@Param("estudanteId") Long estudanteId, @Param("tipo") TransacaoTipo tipo);
} 