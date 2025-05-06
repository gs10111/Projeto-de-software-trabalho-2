package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.DTO.LoginDTO;
import com.moeda.moedaestudantil.DTO.MensagemResponseDTO;
import com.moeda.moedaestudantil.DTO.TokenDTO;
import com.moeda.moedaestudantil.Models.Usuario;
import com.moeda.moedaestudantil.Services.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    /**
     * Endpoint para login de usuários
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            TokenDTO token = autenticacaoService.autenticar(loginDTO.getEmail(), loginDTO.getSenha());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MensagemResponseDTO("Credenciais inválidas"));
        }
    }

    /**
     * Endpoint para verificação de token
     */
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarToken(@RequestParam String token) {
        try {
            boolean valido = autenticacaoService.validarToken(token);
            return ResponseEntity.ok(new MensagemResponseDTO(valido ? "Token válido" : "Token inválido"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensagemResponseDTO("Erro ao verificar token: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para obter informações do usuário logado
     */
    @GetMapping("/me")
    public ResponseEntity<?> getUsuarioInfo(@RequestHeader("Authorization") String authorization) {
        try {
            // Obter token do cabeçalho (remove o prefixo "Bearer ")
            String token = authorization.substring(7);
            
            // Buscar usuário pelo token
            Usuario usuario = autenticacaoService.getUsuarioByToken(token);
            
            // Remover a senha por segurança
            usuario.setSenhaHash(null);
            
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new MensagemResponseDTO("Não autorizado: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para logout (invalidação de token)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorization) {
        try {
            // Obter token do cabeçalho
            String token = authorization.substring(7);
            
            // Invalidar o token
            autenticacaoService.invalidarToken(token);
            
            return ResponseEntity.ok(new MensagemResponseDTO("Logout realizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MensagemResponseDTO("Erro ao realizar logout: " + e.getMessage()));
        }
    }
} 