package com.tortilleria.app_tortilleria.controller;

import com.tortilleria.app_tortilleria.model.Usuario;
import com.tortilleria.app_tortilleria.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarUsuario(usuario));

    }

    @GetMapping("/registros")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<List<Usuario>> listarUsuarios() {

        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> usuarioRegistrado(@PathVariable Long id) {

            return ResponseEntity.status(HttpStatus.OK).body(usuarioService.usuarioPorId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {

        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {

        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.actualizarUsuario(id, usuario));

    }
}
