package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Models.Cupom;
import com.moeda.moedaestudantil.Models.Estudante;
import com.moeda.moedaestudantil.Models.Transacao;
import com.moeda.moedaestudantil.Models.Vantagem;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void enviarEmailTransacao(Transacao transacao) {
        Estudante estudante = transacao.getEstudante();
        if (estudante != null && estudante.getEmail() != null) {
            String texto = String.format(
                    "[LOG] Email para: %s\nAssunto: Moeda Estudantil - Nova transação recebida\n\nOlá %s,\n\nVocê recebeu %d moedas de %s.\n\nMotivo: %s\n\nSeu saldo atual é de %d moedas.\n\nAtenciosamente,\nEquipe Moeda Estudantil",
                    estudante.getEmail(),
                    estudante.getNome(),
                    transacao.getValor(),
                    transacao.getEmissor() != null ? transacao.getEmissor().getNome() : "Sistema",
                    transacao.getDescricao(),
                    estudante.getSaldo());
            
            System.out.println(texto);
        }
    }

    public void enviarEmailResgateCupom(Cupom cupom) {
        Estudante estudante = cupom.getEstudante();
        if (estudante != null && estudante.getEmail() != null) {
            String texto = String.format(
                    "[LOG] Email para: %s\nAssunto: Moeda Estudantil - Cupom resgatado com sucesso\n\nOlá %s,\n\nVocê resgatou um cupom para a vantagem '%s'.\n\nCódigo do cupom: %s\n\nData de resgate: %s\n\nValor: %d moedas\n\nApresente este código no estabelecimento para utilizar sua vantagem.\n\nAtenciosamente,\nEquipe Moeda Estudantil",
                    estudante.getEmail(),
                    estudante.getNome(),
                    cupom.getVantagem().getNome(),
                    cupom.getCodigo(),
                    cupom.getDataResgate().format(FORMATTER),
                    cupom.getValor());
            
            System.out.println(texto);
        }
    }
    
    public void enviarEmailUsoCupom(Cupom cupom) {
        Estudante estudante = cupom.getEstudante();
        if (estudante != null && estudante.getEmail() != null) {
            String texto = String.format(
                    "[LOG] Email para: %s\nAssunto: Moeda Estudantil - Cupom utilizado\n\nOlá %s,\n\nSeu cupom para a vantagem '%s' foi utilizado com sucesso.\n\nCódigo do cupom: %s\n\nData de uso: %s\n\nAgradecemos por utilizar nosso sistema.\n\nAtenciosamente,\nEquipe Moeda Estudantil",
                    estudante.getEmail(),
                    estudante.getNome(),
                    cupom.getVantagem().getNome(),
                    cupom.getCodigo(),
                    cupom.getDataUso().format(FORMATTER));
            
            System.out.println(texto);
        }
    }
    
    public void enviarEmailConfirmacaoEmpresa(String email, String nome, String token) {
        String texto = String.format(
                "[LOG] Email para: %s\nAssunto: Moeda Estudantil - Confirmação de cadastro\n\nOlá %s,\n\nBem-vindo ao sistema Moeda Estudantil. Para confirmar seu cadastro, utilize o seguinte token:\n\n%s\n\nAtenciosamente,\nEquipe Moeda Estudantil",
                email, nome, token);
        
        System.out.println(texto);
    }
} 