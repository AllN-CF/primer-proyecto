package com.tortilleria.app_tortilleria.service;

import com.tortilleria.app_tortilleria.dto.UsuarioDTO;
import com.tortilleria.app_tortilleria.exception.RecursoNoEncontradoException;
import com.tortilleria.app_tortilleria.model.UserRole;
import com.tortilleria.app_tortilleria.model.Usuario;
import com.tortilleria.app_tortilleria.repository.UsuarioRepository;
import com.tortilleria.app_tortilleria.security.JwtService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UsuarioDTO registrarUsuario(Usuario usuario) {
        if (usuario.getId() != null && usuarioRepository.existsById(usuario.getId())) {
            throw new IllegalArgumentException("El usuario no debe tener un ID al registrarse.");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        String token = jwtService.generarToken(userDetailsService.loadUserByUsername(usuario.getEmail()));

        return new UsuarioDTO(usuario, token);
    }

    public Page<UsuarioDTO> obtenerTodos(int page, int size) {
        Page<Usuario> usuariosPage = usuarioRepository.findAll(PageRequest.of(page, size));

        return usuariosPage.map(UsuarioDTO::new);
    }

    public UsuarioDTO usuarioPorId(Long id) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario con el ID " + id + " no existe."));

        return new UsuarioDTO(usuarioExistente);
    }

    public Usuario usuarioPorEmail(String email) {

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay un usuario registrado con el email: " + email));
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RecursoNoEncontradoException("El usuario con el ID " + id + " no existe.");

        usuarioRepository.deleteById(id);
    }

    public UsuarioDTO actualizarUsuario(Long id, Usuario usuarioRequest) {
        Usuario usuarioActual = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario con el ID " + id + " no existe."));

        if (usuarioRequest.getNombre() == null || usuarioRequest.getEmail() == null ||
                usuarioRequest.getDireccion() == null || usuarioRequest.getTelefono() == null)
            throw new IllegalArgumentException("Todos los campos deben ser llenados correctamente.");

        usuarioActual.setNombre(usuarioRequest.getNombre());
        usuarioActual.setEmail(usuarioRequest.getEmail());
        usuarioActual.setDireccion(usuarioRequest.getDireccion());
        usuarioActual.setTelefono(usuarioRequest.getTelefono());
        usuarioRepository.save(usuarioActual);

        return new UsuarioDTO(usuarioActual);
    }

    public Usuario actualizarRolUsuario(Long id, String role) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario con el ID " + id + " no existe."));

        String roleLimpio = role.trim().toUpperCase();
        try {
            usuarioExistente.setRole(UserRole.valueOf(roleLimpio));
            return usuarioRepository.save(usuarioExistente);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol invalido. Valores permitidos: ROLE_CLIENTE, ROLE_REPARTIDOR," +
                    " ROLE_GESTOR, ROLE_ADMIN");
        }
    }

    public void actualizarPassword(Long id, String passwordActual, String nuevoPassword) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario con el ID " + id + " no existe."));

        if (passwordEncoder.matches(passwordActual, usuarioExistente.getPassword())) {

            usuarioExistente.setPassword(passwordEncoder.encode(nuevoPassword));
            usuarioRepository.save(usuarioExistente);

        } else throw new IllegalArgumentException("Contraseña actual incorrecta");
    }
}
