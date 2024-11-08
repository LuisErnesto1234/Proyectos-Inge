package com.tesis.proyecto.proyectot.models.service;

import com.tesis.proyecto.proyectot.models.entity.Usuario;
import com.tesis.proyecto.proyectot.models.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void update(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario register(Usuario usuario) {
        usuario.setContrasennia(passwordEncoder.encode(usuario.getContrasennia())); // Codifica la contraseña
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String correo, String contrasennia) {
        return usuarioRepository.findByCorreo(correo)
                .filter(u -> passwordEncoder.matches(contrasennia, u.getContrasennia()));
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new User(usuario.getCorreo(), usuario.getContrasennia(), new ArrayList<>());
    }
}

