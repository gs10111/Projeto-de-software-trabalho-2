package com.moeda.moedaestudantil.Controllers;

import com.moeda.moedaestudantil.Models.Usuario;
import com.moeda.moedaestudantil.Repositories.InstituicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @Autowired
    private InstituicaoRepository instituicaoRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        if (role.equals("ROLE_PROFESSOR")) {
            return "redirect:/professor/dashboard";
        } else if (role.equals("ROLE_ALUNO")) {
            return "redirect:/estudante/dashboard";
        } else if (role.equals("ROLE_PARCEIRO")) {
            return "redirect:/empresa/dashboard";
        }
        
        return "redirect:/login";
    }

}