package com.moeda.moedaestudantil.config;

import com.moeda.moedaestudantil.Services.AutenticacaoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AutenticacaoService autenticacaoService;

    public JwtAuthenticationFilter(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        // Se não tem token ou não começa com Bearer, continua a cadeia de filtros
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extrair o token do header (remover "Bearer ")
            String jwt = authorizationHeader.substring(7);
            
            // Se o token é válido, autenticar o usuário
            if (autenticacaoService.validarToken(jwt)) {
                // Obter usuário pelo token
                UserDetails userDetails = autenticacaoService.loadUserByUsername(
                        autenticacaoService.getUsuarioByToken(jwt).getEmail());

                // Criar um token de autenticação
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Definir o contexto de segurança com o usuário autenticado
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Não foi possível validar o token", e);
        }

        // Continuar a cadeia de filtros
        filterChain.doFilter(request, response);
    }
} 