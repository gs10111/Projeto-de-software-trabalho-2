package com.moeda.moedaestudantil.Models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EMPRESA")
public class EmpresaParceira extends Usuario {
    
}
