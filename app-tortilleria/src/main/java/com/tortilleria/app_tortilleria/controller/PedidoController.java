package com.tortilleria.app_tortilleria.controller;

import com.tortilleria.app_tortilleria.dto.PedidoRequestDTO;
import com.tortilleria.app_tortilleria.dto.PedidoDTO;
import com.tortilleria.app_tortilleria.model.Usuario;
import com.tortilleria.app_tortilleria.service.PedidoService;
import com.tortilleria.app_tortilleria.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/nuevo")
    public ResponseEntity<PedidoDTO> nuevoPedido(@RequestBody PedidoRequestDTO pedido, Authentication authentication) {

        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(usuarioLogueado.getId(), pedido));
    }

    @GetMapping("/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PedidoDTO>> verTodosLosPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String estado
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(pedidoService.todosLosPedidos(page, size, estado));
    }

    @GetMapping("/mis-pedidos")
    public ResponseEntity<Page<PedidoDTO>> pedidosPorCliente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(authentication.getName());

        return ResponseEntity.status(HttpStatus.OK).body(pedidoService.historialPedidos(usuarioLogueado, page, size));
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoDTO> verPedido(
            @PathVariable Long idPedido,
            Authentication authentication
    ) {

        boolean tienePermiso = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                || a.getAuthority().equals("ROLE_GESTOR")
                || a.getAuthority().equals("ROLE_REPARTIDOR"));

        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(authentication.getName());

        if (!tienePermiso && !pedidoService.esDelUsuario(usuarioLogueado.getId(), idPedido))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.status(HttpStatus.OK).body(pedidoService.obtenerPedido(idPedido));
    }

    @DeleteMapping("/{idPedido}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> borrarPedido(@PathVariable Long idPedido) {
        pedidoService.eliminarPedido(idPedido);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<PedidoDTO> cambiarPedido(
            @PathVariable Long idPedido,
            @RequestBody PedidoRequestDTO actualizacion,
            Authentication authentication) {

        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(authentication.getName());

        if (!pedidoService.esDelUsuario(usuarioLogueado.getId(), idPedido))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(pedidoService.actualizarPedido(idPedido, actualizacion));
    }

    @PatchMapping("/{idPedido}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'REPARTIDOR')")
    public ResponseEntity<PedidoDTO> cambiarEstadoPedido(
            @PathVariable Long idPedido,
            @RequestBody EstadoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pedidoService.actualizarEstadoPedido(idPedido, request.getEstado()));
    }

    public static class EstadoRequest {
        private String estado;

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}