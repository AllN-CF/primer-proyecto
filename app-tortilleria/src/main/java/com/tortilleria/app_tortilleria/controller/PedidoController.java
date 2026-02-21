package com.tortilleria.app_tortilleria.controller;

import com.tortilleria.app_tortilleria.dto.PedidoRequestDTO;
import com.tortilleria.app_tortilleria.model.EstadoPedido;
import com.tortilleria.app_tortilleria.model.Pedido;
import com.tortilleria.app_tortilleria.dto.PedidoDTO;
import com.tortilleria.app_tortilleria.model.Usuario;
import com.tortilleria.app_tortilleria.service.PedidoService;
import com.tortilleria.app_tortilleria.service.UsuarioService;
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
        String emailLogueado = authentication.getName();
        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(emailLogueado);

        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crearPedido(usuarioLogueado.getId(), pedido));
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoDTO> verPedido(
            @PathVariable Long idPedido,
            Authentication authentication
    ) {
        String emailLogueado = authentication.getName();

        boolean tienePermiso = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                || a.getAuthority().equals("ROLE_GESTOR")
                || a.getAuthority().equals("ROLE_REPARTIDOR"));

        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(emailLogueado);

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

        String emailLogueado = authentication.getName();

        Usuario usuarioLogueado = usuarioService.usuarioPorEmail(emailLogueado);

        if (!pedidoService.esDelUsuario(usuarioLogueado.getId(), idPedido))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(pedidoService.actualizarPedido(idPedido, actualizacion));
    }

    @PatchMapping("/{idPedido}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR', 'REPARTIDOR')")
    public ResponseEntity<PedidoDTO> cambiarEstadoPedido(
            @PathVariable Long idPedido,
            @RequestBody EstadoPedido estado
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(pedidoService.actualizarEstadoPedido(idPedido, estado));
    }
}