package com.moeda.moedaestudantil.config;

import com.moeda.moedaestudantil.Services.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// Add these missing imports
import jakarta.persistence.Column;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AutenticacaoService autenticacaoService;
    
    // Definir o passwordEncoder como um campo estático para evitar referência circular
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PASSWORD_ENCODER;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/", "/home", "/login", "/cadastro/**").permitAll()
                .requestMatchers("/professor/**").hasRole("PROFESSOR")
                .requestMatchers("/estudante/**").hasRole("ALUNO")
                .requestMatchers("/empresa/**").hasRole("PARCEIRO")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", false) // Add false to prevent always redirecting to this URL
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                            Authentication authentication) throws IOException, ServletException {
                        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                        for (GrantedAuthority authority : authorities) {
                            if (authority.getAuthority().equals("ROLE_PROFESSOR")) {
                                response.sendRedirect("/professor/dashboard");
                                return;
                            } else if (authority.getAuthority().equals("ROLE_ALUNO")) {
                                response.sendRedirect("/estudante/dashboard");
                                return;
                            } else if (authority.getAuthority().equals("ROLE_PARCEIRO")) {
                                response.sendRedirect("/empresa/dashboard");
                                return;
                            }
                        }
                        response.sendRedirect("/dashboard"); // Fallback redirect
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(autenticacaoService)
            .passwordEncoder(passwordEncoder());
    }

}