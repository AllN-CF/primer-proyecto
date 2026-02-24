package com.tortilleria.app_tortilleria.service;

import com.tortilleria.app_tortilleria.dto.PedidoRequestDTO;
import com.tortilleria.app_tortilleria.exception.RecursoNoEncontradoException;
import com.tortilleria.app_tortilleria.model.*;
import com.tortilleria.app_tortilleria.dto.PedidoDTO;
import com.tortilleria.app_tortilleria.repository.PedidoDetalleRepository;
import com.tortilleria.app_tortilleria.repository.PedidoRepository;
import com.tortilleria.app_tortilleria.repository.ProductoRepository;
import com.tortilleria.app_tortilleria.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         ProductoRepository productoRepository,
                         PedidoDetalleRepository pedidoDetalleRepository)
    {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.pedidoDetalleRepository = pedidoDetalleRepository;
    }

    @Transactional
    public PedidoDTO crearPedido(Long id, PedidoRequestDTO pedido) {
        Usuario usuarioDelPedido = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario con ID " + " no existe."));

        Pedido nuevoPedido = new Pedido(usuarioDelPedido);
        pedidoRepository.save(nuevoPedido);

        List<PedidoDetalle> listaPedidoDetalle = new ArrayList<>();
        double nuevoTotalPedido = 0.0;
        for (PedidoRequestDTO.ProductoPedidoRequest item : pedido.getProductos()) {
            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Este producto no existe."));
            if(!producto.getDisponibilidad())
                throw new RecursoNoEncontradoException("Este producto se encuentra agotado, lamentamos los inconvenientes.");

            listaPedidoDetalle.add(new PedidoDetalle(
                    item.getCantidad(),
                    producto.getPrecio(),
                    producto,
                    nuevoPedido
            ));

            nuevoTotalPedido += item.getCantidad() * producto.getPrecio();
        }
        nuevoPedido.setDetalles(listaPedidoDetalle);

        nuevoPedido.setTotal(nuevoTotalPedido);
        pedidoRepository.save(nuevoPedido);

        return transferirPedido(nuevoPedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> todosLosPedidos() {
        List<Pedido> listaDePedidos = pedidoRepository.findAll();

        List<PedidoDTO> listaPedidosDTO = new ArrayList<>();
        for (Pedido pedido : listaDePedidos) {

            listaPedidosDTO.add(transferirPedido(pedido));
        }

        return listaPedidosDTO;
    }

    @Transactional(readOnly = true)
    public PedidoDTO obtenerPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un pedido con ID: " + idPedido));

        return transferirPedido(pedido);
    }

    public void eliminarPedido(Long id) {
        if (!pedidoRepository.existsById(id))
            throw new RecursoNoEncontradoException("El pedido con ID: " + id + "no existe.");

        pedidoRepository.deleteById(id);
    }

    @Transactional
    public PedidoDTO actualizarPedido(Long id, PedidoRequestDTO actualizacion) {

        Pedido pedidoActual = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un pedido con ID: " + id));

        if (pedidoActual.getEstado().name().equals("EN_CAMINO"))
            throw new IllegalArgumentException("El pedido ya se encuentra en camino, por lo tanto, ya no puede modificarse.");

        List<PedidoDetalle> detallesActualizados = pedidoActual.getDetalles();
        double nuevoTotal = pedidoActual.getTotal();

        listaRequest:
        for (PedidoRequestDTO.ProductoPedidoRequest itemNuevo : actualizacion.getProductos()) {
            for (PedidoDetalle productoPedido : pedidoActual.getDetalles()) {
                int cantidadActual = productoPedido.getCantidad();

                if ((long) itemNuevo.getIdProducto() == productoPedido.getProducto().getId()) {
                    productoPedido.setCantidad(cantidadActual += itemNuevo.getCantidad());
                    nuevoTotal += itemNuevo.getCantidad() * productoPedido.getProducto().getPrecio();
                    continue listaRequest;
                }
            }
            Producto producto = productoRepository.findById(itemNuevo.getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Este producto no existe."));
            detallesActualizados.add(new PedidoDetalle(
                    itemNuevo.getCantidad(),
                    producto.getPrecio(),
                    producto,
                    pedidoActual
            ));
            nuevoTotal += itemNuevo.getCantidad() * producto.getPrecio();
        }
        pedidoActual.setTotal(nuevoTotal);
        pedidoActual.setDetalles(detallesActualizados);
        pedidoRepository.save(pedidoActual);

        return transferirPedido(pedidoActual);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> historialPedidos(Usuario usuario) {
        List<Pedido> listaDePedidos = pedidoRepository.findAllByUsuario(usuario);

        List<PedidoDTO> listaPedidosDTO = new ArrayList<>();
        for (Pedido pedido : listaDePedidos)
            listaPedidosDTO.add(transferirPedido(pedido));

        return listaPedidosDTO;
    }

    @Transactional
    public PedidoDTO actualizarEstadoPedido(Long idPedido, String estado) {
        Pedido pedidoExistente = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un pedido con ID: " + idPedido));

        String estadoLimpio = estado.trim().toUpperCase();
        try {
            pedidoExistente.setEstado(EstadoPedido.valueOf(estadoLimpio));
            pedidoRepository.save(pedidoExistente);

            return transferirPedido(pedidoExistente);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado invalido. Valores permitidos: PENDIENTE, EN_CAMINO, ENTREGADO, CANCELADO");
        }
    }

    public boolean esDelUsuario(Long idUsuario, Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un pedido con ID: " + idPedido));

        return idUsuario.equals(pedido.getUsuario().getId());
    }

    private PedidoDTO transferirPedido(Pedido pedido) {

        List<PedidoDTO.DetallesPedido> listaDetalles = new ArrayList<>();
        for (PedidoDetalle item : pedido.getDetalles()) {

            listaDetalles.add(new PedidoDTO.DetallesPedido(
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    item.getProducto().getPrecio()
            ));
        }
        return new PedidoDTO(
                pedido.getId(),
                pedido.getFecha(),
                pedido.getUsuario().getNombre(),
                pedido.getUsuario().getDireccion(),
                pedido.getUsuario().getTelefono(),
                listaDetalles,
                pedido.getTotal(),
                pedido.getEstado()
        );
    }
}