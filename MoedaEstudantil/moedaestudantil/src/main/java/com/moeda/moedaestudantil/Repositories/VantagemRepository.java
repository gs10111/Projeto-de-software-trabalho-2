package com.moeda.moedaestudantil.Repositories;

import com.moeda.moedaestudantil.Models.Vantagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VantagemRepository extends JpaRepository<Vantagem, Long> {
    
    List<Vantagem> findByEmpresaId(Long empresaId);
    
    Page<Vantagem> findByDisponivelTrue(Pageable pageable);
    
    Page<Vantagem> findByEmpresaIdAndDisponivelTrue(Long empresaId, Pageable pageable);
    
    Page<Vantagem> findByValorLessThanEqualAndDisponivelTrue(Integer valor, Pageable pageable);
    
    Page<Vantagem> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome, Pageable pageable);
    
    Page<Vantagem> findByEmpresaIdAndValorLessThanEqualAndDisponivelTrue(Long empresaId, Integer valor, Pageable pageable);
    
    Page<Vantagem> findByEmpresaIdAndNomeContainingIgnoreCaseAndDisponivelTrue(Long empresaId, String nome, Pageable pageable);
    
    Page<Vantagem> findByValorLessThanEqualAndNomeContainingIgnoreCaseAndDisponivelTrue(Integer valor, String nome, Pageable pageable);
    
    Page<Vantagem> findByEmpresaIdAndValorLessThanEqualAndNomeContainingIgnoreCaseAndDisponivelTrue(
            Long empresaId, Integer valor, String nome, Pageable pageable);
    
    List<Vantagem> findByEmpresaIdAndIdNotAndDisponivelTrue(Long empresaId, Long id, Pageable pageable);
    
    List<Vantagem> findByEmpresaIdAndDisponivelTrue(Long empresaId);
} 