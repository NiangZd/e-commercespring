package com.projetosapatos.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projetosapatos.demo.aplicacao.Usuario;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findUsuarioByUsername(String username);
}
