package com.tortilleria.app_tortilleria.service;

import com.tortilleria.app_tortilleria.model.Usuario;
import com.tortilleria.app_tortilleria.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario registrarUsuario(Usuario usuario) throws IllegalArgumentException {
        if (usuario.getId() == null) {
            return usuarioRepository.save(usuario);
        } else {
            throw new IllegalArgumentException("El usuario no debe tener un ID al registrarse.");
        }
    }
}
