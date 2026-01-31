package com.tortilleria.app_tortilleria.service;

import com.tortilleria.app_tortilleria.exception.RecursoNoEncontradoException;
import com.tortilleria.app_tortilleria.model.Usuario;
import com.tortilleria.app_tortilleria.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuario.getId() != null && usuarioRepository.existsById(usuario.getId())) {
            throw new IllegalArgumentException("El usuario no debe tener un ID al registrarse.");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario usuarioPorId(Long id) {
        Optional<Usuario> existeUsuario = usuarioRepository.findById(id);

        if (existeUsuario.isPresent()) return existeUsuario.get();
        else throw new RecursoNoEncontradoException("El usuario con el ID " + id + " no existe.");
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RecursoNoEncontradoException("El usuario con el ID " + id + " no existe.");

        usuarioRepository.deleteById(id);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuario) {
        Optional<Usuario> actualUsuario = usuarioRepository.findById(id);

        if (actualUsuario.isPresent()) {
            Usuario usuarioTemporal = actualUsuario.get();

            if (usuario.getNombre() == null || usuario.getEmail() == null ||
                usuario.getDireccion() == null || usuario.getTelefono() == null)
                throw new IllegalArgumentException("Todos los campos deben ser llenados correctamente.");

            usuarioTemporal.setNombre(usuario.getNombre());
            usuarioTemporal.setEmail(usuario.getEmail());
            usuarioTemporal.setDireccion(usuario.getDireccion());
            usuarioTemporal.setTelefono(usuario.getTelefono());

            return usuarioRepository.save(usuarioTemporal);
        }
        else throw new RecursoNoEncontradoException("El usuario con el ID " + id + " no existe.");
    }
}
