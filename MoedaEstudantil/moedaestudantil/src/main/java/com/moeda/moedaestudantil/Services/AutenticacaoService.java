package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.DTO.TokenDTO;
import com.moeda.moedaestudantil.Models.Usuario;
import com.moeda.moedaestudantil.Repositories.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Value("${jwt.secret:meusegredojwt12345}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Map<String, String> tokenBlacklist = new HashMap<>();
    
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    public AutenticacaoService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
        
        return new User(
                usuario.getEmail(),
                usuario.getSenhaHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()))
        );
    }
    
    public TokenDTO autenticar(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
        
        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new RuntimeException("Senha inválida");
        }
        
        String token = gerarToken(usuario);
        
        return new TokenDTO(token, usuario.getId(), usuario.getNome(), usuario.getPerfil());
    }

    public String obterPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
        
        return usuario.getPerfil().name();
    }
    
    public String gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("userId", usuario.getId())
                .claim("perfil", usuario.getPerfil().name())
                .setIssuedAt(agora)
                .setExpiration(expiracao)
                .signWith(key)
                .compact();
    }
    
    public boolean validarToken(String token) {
        try {
            // Verificar se o token está na blacklist
            if (tokenBlacklist.containsKey(token)) {
                return false;
            }
            
            // Validar o token
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Usuario getUsuarioByToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        String email = claims.getSubject();
        
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
    
    public void invalidarToken(String token) {
        // Adicionar token na blacklist
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        Date expiracao = claims.getExpiration();
        tokenBlacklist.put(token, expiracao.toString());
    }
} 