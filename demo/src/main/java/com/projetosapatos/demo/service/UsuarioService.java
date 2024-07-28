package com.projetosapatos.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.projetosapatos.demo.aplicacao.Usuario;
import com.projetosapatos.demo.repository.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> optional = repository.findUsuarioByUsername(username);
        if(optional.isPresent()) {
        return optional.get();
        }
        throw new UsernameNotFoundException("User not found");
    }
}