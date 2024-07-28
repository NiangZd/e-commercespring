package com.projetosapatos.demo.service;

import org.springframework.stereotype.Service;
import com.projetosapatos.demo.aplicacao.Sapato;
import com.projetosapatos.demo.repository.SapatoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Date; 
import java.util.Optional;

@Service
public class SapatoService {
    private final SapatoRepository repository;

    public SapatoService(SapatoRepository repository) {
        this.repository = repository;
    }

    public List<Sapato> findAll() {
        return repository.findAll();
    }

    public List<Sapato> findAllIncludingDeleted() {
        return repository.findAll();
    }

    public Optional<Sapato> findById(String id) {
        return repository.findById(id);
    }

    public Sapato create(Sapato sapato) {
        return repository.save(sapato);
    }

    public Sapato update(Sapato sapato) {
        return repository.saveAndFlush(sapato);
    }

    public void delete(String id) {
        Optional<Sapato> sapatoOptional = repository.findById(id);
        if (sapatoOptional.isPresent()) {
            Sapato sapato = sapatoOptional.get();
            java.util.Date utilDate = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
    
            sapato.setIsDeletedNew(sqlDate);
            repository.save(sapato); 
        }
    }
}
