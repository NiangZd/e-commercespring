package com.projetosapatos.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projetosapatos.demo.aplicacao.Sapato;

import java.util.List;
import java.util.UUID;

public interface SapatoRepository extends JpaRepository<Sapato, String> {
    
}