package com.moeda.moedaestudantil.Services;

import com.moeda.moedaestudantil.Models.Usuario;
import com.moeda.moedaestudantil.Repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    
    public boolean autenticar(String email, String senha) {
        try {
            UserDetails userDetails = loadUserByUsername(email);
            return userDetails.getPassword().equals(senha);
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    public String obterPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
        
        return usuario.getPerfil().name();
    }
} 