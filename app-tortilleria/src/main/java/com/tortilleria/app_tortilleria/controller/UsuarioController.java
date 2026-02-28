package com.tortilleria.app_tortilleria.controller;

import com.tortilleria.app_tortilleria.dto.UsuarioDTO;
import com.tortilleria.app_tortilleria.model.Usuario;
import com.tortilleria.app_tortilleria.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody Usuario usuario) {

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrarUsuario(usuario));
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<Page<UsuarioDTO>> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.obtenerTodos(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> usuarioRegistrado(@PathVariable Long id, Authentication authentication) {

        boolean tienePermiso = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_GESTOR")
                        || a.getAuthority().equals("ROLE_REPARTIDOR"));

        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(authentication.getName());

        if (!tienePermiso && !usuarioLogueado.getId().equals(id))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.usuarioPorId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {

        usuarioService.eliminarUsuario(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/actualizar-datos")
    public ResponseEntity<UsuarioDTO> actualizarUsuario(
            @RequestBody Usuario usuarioRequest,
            Authentication authentication
    ) {
        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(authentication.getName());

        return ResponseEntity.status(HttpStatus.OK)
                .body(usuarioService.actualizarUsuario(usuarioLogueado.getId(), usuarioRequest));
    }

    @PatchMapping("/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> cambiarRoleUsuario(
            @PathVariable Long id,
            @RequestBody RoleRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.actualizarRolUsuario(id, request.getRole()));
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> cambiarPassword(@RequestBody PasswordRequest request, Authentication authentication) {

        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(authentication.getName());
        usuarioService.actualizarPassword(usuarioLogueado.getId(), request.passwordActual, request.nuevoPassword);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public static class RoleRequest {
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class PasswordRequest {
        private String passwordActual;
        private String nuevoPassword;

        public String getPasswordActual() {
            return passwordActual;
        }

        public void setPasswordActual(String passwordActual) {
            this.passwordActual = passwordActual;
        }

        public String getNuevoPassword() {
            return nuevoPassword;
        }

        public void setNuevoPassword(String nuevoPassword) {
            this.nuevoPassword = nuevoPassword;
        }
    }
}
