package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Vantagem;
import com.moeda.moedaestudantil.Repositories.EmpresaParceiraRepository;
import com.moeda.moedaestudantil.Repositories.VantagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VantagemService {

    @Autowired
    private VantagemRepository vantagemRepository;

    @Autowired
    private EmpresaParceiraRepository empresaParceiraRepository;

    @Autowired
    private EmpresaParceiraService empresaService;

    public Vantagem buscarPorId(Long id) {
        return vantagemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vantagem não encontrada"));
    }

    public List<Vantagem> listarTodasVantagens() {
        return vantagemRepository.findAll();
    }

    public List<Vantagem> listarVantagensPorEmpresa(Long empresaId) {
        return vantagemRepository.findByEmpresaId(empresaId);
    }
    
    /**
     * Buscar vantagens de uma empresa com paginação
     */
    public Page<Vantagem> buscarPorEmpresa(Long empresaId, Pageable pageable) {
        return vantagemRepository.findByEmpresaId(empresaId, pageable);
    }
    
    /**
     * Salvar uma vantagem
     */
    @Transactional
    public Vantagem salvar(Vantagem vantagem) {
        return vantagemRepository.save(vantagem);
    }
    
    /**
     * Remover uma vantagem
     */
    @Transactional
    public void remover(Long id) {
        Vantagem vantagem = buscarPorId(id);
        vantagemRepository.delete(vantagem);
    }
    
    /**
     * Atualizar a disponibilidade de uma vantagem
     */
    @Transactional
    public Vantagem atualizarDisponibilidade(Long id, boolean disponivel) {
        Vantagem vantagem = buscarPorId(id);
        vantagem.setDisponivel(disponivel);
        return vantagemRepository.save(vantagem);
    }
    
    public Vantagem criarVantagem(Vantagem vantagem, Long empresaId) {
        EmpresaParceira empresa = empresaParceiraRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        
        vantagem.setEmpresa(empresa);
        return vantagemRepository.save(vantagem);
    }

    public Vantagem atualizarVantagem(Long id, Vantagem vantagemAtualizada) {
        Vantagem vantagemExistente = buscarPorId(id);
        
        // Atualizar apenas os campos permitidos
        vantagemExistente.setNome(vantagemAtualizada.getNome());
        vantagemExistente.setDescricao(vantagemAtualizada.getDescricao());
        vantagemExistente.setValor(vantagemAtualizada.getValor());
        vantagemExistente.setDisponivel(vantagemAtualizada.isDisponivel());
        vantagemExistente.setImagemUrl(vantagemAtualizada.getImagemUrl());
        
        return vantagemRepository.save(vantagemExistente);
    }

    public void excluirVantagem(Long id) {
        Vantagem vantagem = buscarPorId(id);
        vantagemRepository.delete(vantagem);
    }

    public List<Vantagem> listarVantagensEmDestaque(int quantidade) {
        Pageable pageable = PageRequest.of(0, quantidade, Sort.by("valor"));
        return vantagemRepository.findByDisponivelTrue(pageable).getContent();
    }

    public Page<Vantagem> buscarVantagensDisponiveis(Long empresaId, Integer valorMaximo, String pesquisa, Pageable pageable) {
        if (empresaId != null && valorMaximo != null && pesquisa != null && !pesquisa.isEmpty()) {
            return vantagemRepository.findByEmpresaIdAndValorLessThanEqualAndNomeContainingIgnoreCaseAndDisponivelTrue(
                    empresaId, valorMaximo, pesquisa, pageable);
        } else if (empresaId != null && valorMaximo != null) {
            return vantagemRepository.findByEmpresaIdAndValorLessThanEqualAndDisponivelTrue(
                    empresaId, valorMaximo, pageable);
        } else if (empresaId != null && pesquisa != null && !pesquisa.isEmpty()) {
            return vantagemRepository.findByEmpresaIdAndNomeContainingIgnoreCaseAndDisponivelTrue(
                    empresaId, pesquisa, pageable);
        } else if (valorMaximo != null && pesquisa != null && !pesquisa.isEmpty()) {
            return vantagemRepository.findByValorLessThanEqualAndNomeContainingIgnoreCaseAndDisponivelTrue(
                    valorMaximo, pesquisa, pageable);
        } else if (empresaId != null) {
            return vantagemRepository.findByEmpresaIdAndDisponivelTrue(empresaId, pageable);
        } else if (valorMaximo != null) {
            return vantagemRepository.findByValorLessThanEqualAndDisponivelTrue(valorMaximo, pageable);
        } else if (pesquisa != null && !pesquisa.isEmpty()) {
            return vantagemRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue(pesquisa, pageable);
        } else {
            return vantagemRepository.findByDisponivelTrue(pageable);
        }
    }

    public List<Vantagem> buscarOutrasVantagensDaEmpresa(Long empresaId, Long vantagemAtualId, int quantidade) {
        Pageable pageable = PageRequest.of(0, quantidade);
        return vantagemRepository.findByEmpresaIdAndIdNotAndDisponivelTrue(empresaId, vantagemAtualId, pageable);
    }
} 