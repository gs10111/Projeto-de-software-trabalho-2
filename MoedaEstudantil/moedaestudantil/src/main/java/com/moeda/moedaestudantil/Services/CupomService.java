package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Enumerators.CupomStatus;
import com.moeda.moedaestudantil.Enumerators.TransacaoTipo;
import com.moeda.moedaestudantil.Models.Cupom;
import com.moeda.moedaestudantil.Models.EmpresaParceira;
import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Models.Vantagem;
import com.moeda.moedaestudantil.Repositories.CupomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CupomService {

    @Autowired
    private CupomRepository cupomRepository;
    
    @Autowired
    private EstudanteService estudanteService;
    
    @Autowired
    private VantagemService vantagemService;
    
    @Autowired
    private TransacaoService transacaoService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private EmpresaParceiraService empresaService;
    
    @Transactional
    public Cupom gerarCupom(Long estudanteId, Long vantagemId) {
        Estudante estudante = estudanteService.buscarPorId(estudanteId);
        Vantagem vantagem = vantagemService.buscarPorId(vantagemId);
        
        // Verificar se a vantagem está disponível
        if (!vantagem.isDisponivel()) {
            throw new IllegalArgumentException("Esta vantagem não está disponível no momento.");
        }
        
        // Verificar se o estudante tem saldo suficiente
        if (estudante.getSaldo() < vantagem.getValor()) {
            throw new IllegalArgumentException(
                    "Saldo insuficiente. Você precisa de " + vantagem.getValor() + " moedas para esta vantagem.");
        }
        
        // Gerar código único para o cupom
        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Criar e salvar o cupom
        Cupom cupom = new Cupom();
        cupom.setEstudante(estudante);
        cupom.setVantagem(vantagem);
        cupom.setEmpresa(vantagem.getEmpresa());
        cupom.setCodigo(codigo);
        cupom.setDataResgate(LocalDateTime.now());
        cupom.setStatus(CupomStatus.ATIVO);
        cupom.setValor(vantagem.getValor());
        
        // Debitar o valor da conta do estudante
        estudanteService.atualizarSaldo(estudanteId, -vantagem.getValor());
        
        // Registrar a transação
        transacaoService.registrarTransacao(
                null, 
                estudanteId, 
                vantagem.getValor(), 
                TransacaoTipo.RESGATE, 
                "Resgate de vantagem: " + vantagem.getNome(), 
                vantagem.getId());
        
        // Salvar cupom
        Cupom cupomSalvo = cupomRepository.save(cupom);
        
        // Enviar email de confirmação
        emailService.enviarEmailResgateCupom(cupomSalvo);
        
        return cupomSalvo;
    }
    
    public Page<Cupom> buscarPorEstudante(Long estudanteId, Pageable pageable) {
        return cupomRepository.findByEstudanteId(estudanteId, pageable);
    }
    
    public Page<Cupom> buscarPorEstudanteEmpresa(Long estudanteId, Long empresaId, Pageable pageable) {
        return cupomRepository.findByEstudanteIdAndEmpresaId(estudanteId, empresaId, pageable);
    }
    
    public Page<Cupom> buscarPorEstudanteStatus(Long estudanteId, CupomStatus status, Pageable pageable) {
        return cupomRepository.findByEstudanteIdAndStatus(estudanteId, status, pageable);
    }
    
    public Page<Cupom> buscarPorEstudanteEmpresaStatus(Long estudanteId, Long empresaId, CupomStatus status, Pageable pageable) {
        return cupomRepository.findByEstudanteIdAndEmpresaIdAndStatus(estudanteId, empresaId, status, pageable);
    }
    
    public int contarCuponsAtivosPorEstudante(Long estudanteId) {
        return cupomRepository.countByEstudanteIdAndStatus(estudanteId, CupomStatus.ATIVO);
    }
    
    @Transactional
    public Cupom validarCupom(String codigo, Long empresaId) {
        Cupom cupom = cupomRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));
        
        if (!cupom.getEmpresa().getId().equals(empresaId)) {
            throw new IllegalArgumentException("Este cupom não pertence a esta empresa");
        }
        
        if (cupom.getStatus() != CupomStatus.ATIVO) {
            throw new IllegalArgumentException("Este cupom já foi " + cupom.getStatus().getDescricao().toLowerCase());
        }
        
        cupom.setStatus(CupomStatus.USADO);
        cupom.setDataUso(LocalDateTime.now());
        return cupomRepository.save(cupom);
    }

    public Cupom resgatarVantagem(Estudante estudante, Vantagem vantagem, EmpresaParceira empresa) {
        // Verificar se o estudante tem saldo suficiente
        if (estudante.getSaldo() < vantagem.getValor()) {
            throw new RuntimeException("Saldo insuficiente para resgatar esta vantagem");
        }

        // Gerar código único para o cupom
        String codigo = gerarCodigoUnico();

        // Criar novo cupom
        Cupom cupom = new Cupom(codigo, estudante, vantagem, empresa);
        
        // Atualizar saldo do estudante
        estudante.setSaldo(estudante.getSaldo() - vantagem.getValor());
        
        // Salvar cupom
        Cupom cupomSalvo = cupomRepository.save(cupom);
        
        // Enviar email de confirmação
        emailService.enviarEmailResgateCupom(cupomSalvo);
        
        return cupomSalvo;
    }
    
    public Page<Cupom> listarCuponsPorEstudante(Long estudanteId, Pageable pageable) {
        return cupomRepository.findByEstudanteId(estudanteId, pageable);
    }
    
    public Page<Cupom> listarCuponsPorEstudanteEEmpresa(Long estudanteId, Long empresaId, Pageable pageable) {
        return cupomRepository.findByEstudanteIdAndEmpresaId(estudanteId, empresaId, pageable);
    }
    
    public Page<Cupom> listarCuponsPorEstudanteEStatus(Long estudanteId, CupomStatus status, Pageable pageable) {
        return cupomRepository.findByEstudanteIdAndStatus(estudanteId, status, pageable);
    }
    
    public Page<Cupom> listarCuponsPorEstudanteEmpresaEStatus(Long estudanteId, Long empresaId, CupomStatus status, Pageable pageable) {
        return cupomRepository.findByEstudanteIdAndEmpresaIdAndStatus(estudanteId, empresaId, status, pageable);
    }
    
    public List<Long> listarEmpresasComCuponsPorEstudante(Long estudanteId) {
        return cupomRepository.findDistinctEmpresaIdsByEstudanteId(estudanteId);
    }
    
    public int contarCuponsPorEstudanteEStatus(Long estudanteId, CupomStatus status) {
        return cupomRepository.countByEstudanteIdAndStatus(estudanteId, status);
    }
    
    public List<Cupom> listarCuponsPorEmpresa(Long empresaId) {
        return cupomRepository.findByEmpresaId(empresaId);
    }
    
    public Optional<Cupom> buscarCupomPorCodigo(String codigo) {
        return cupomRepository.findByCodigo(codigo);
    }
    
    public Cupom atualizarStatusCupom(String codigo, CupomStatus novoStatus) {
        Optional<Cupom> cupomOpt = cupomRepository.findByCodigo(codigo);
        if (cupomOpt.isPresent()) {
            Cupom cupom = cupomOpt.get();
            cupom.setStatus(novoStatus);
            
            if (novoStatus == CupomStatus.USADO) {
                cupom.setDataUso(LocalDateTime.now());
                // Enviar email de uso do cupom
                emailService.enviarEmailUsoCupom(cupom);
            }
            
            return cupomRepository.save(cupom);
        } else {
            throw new RuntimeException("Cupom não encontrado com o código: " + codigo);
        }
    }

    private String gerarCodigoUnico() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public Cupom usarCupom(String codigo) {
        Cupom cupom = cupomRepository.findByCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Cupom não encontrado"));
        
        if (cupom.getStatus() != CupomStatus.ATIVO) {
            throw new IllegalArgumentException("Este cupom não está ativo");
        }
        
        if (LocalDateTime.now().isAfter(cupom.getDataValidade())) {
            cupom.setStatus(CupomStatus.EXPIRADO);
            return cupomRepository.save(cupom);
        }
        
        cupom.setStatus(CupomStatus.USADO);
        cupom.setDataUso(LocalDateTime.now());
        
        return cupomRepository.save(cupom);
    }
    
    @Scheduled(cron = "0 0 0 * * ?") // Executa todos os dias à meia-noite
    @Transactional
    public void verificarCuponsExpirados() {
        LocalDateTime agora = LocalDateTime.now();
        List<Cupom> cuponsExpirados = cupomRepository.findByDataValidadeBeforeAndStatus(agora, CupomStatus.ATIVO);
        
        for (Cupom cupom : cuponsExpirados) {
            cupom.setStatus(CupomStatus.EXPIRADO);
            cupomRepository.save(cupom);
        }
    }
} 