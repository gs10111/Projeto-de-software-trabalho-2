package com.moeda.moedaestudantil.Repositories;

import com.moeda.moedaestudantil.Models.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {}
