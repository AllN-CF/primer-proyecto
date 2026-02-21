package com.tortilleria.app_tortilleria.dto;

import com.tortilleria.app_tortilleria.model.EstadoPedido;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {
    private Long id;
    private LocalDateTime fecha;
    private String nombre;
    private String direccion;
    private String telefono;
    private List<DetallesPedido> productos;
    private double total;
    private EstadoPedido estado;

    public PedidoDTO(
            Long id,
            LocalDateTime fecha,
            String nombre,
            String direccion,
            String telefono,
            List<DetallesPedido> productos,
            double total, EstadoPedido estado
    ) {
        this.id = id;
        this.fecha = fecha;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.productos = productos;
        this.total = total;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Long getId() { return id; }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public List<DetallesPedido> getProductos() {
        return productos;
    }

    public void setProductos(List<DetallesPedido> productos) {
        this.productos = productos;
    }

    public static class DetallesPedido {
        private String nombreProducto;
        private int cantidad;
        private double precioUnitario;
        private double subtotal;

        public DetallesPedido(String nombreProducto, int cantidad, double precioUnitario) {
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = cantidad * precioUnitario;
        }

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(double subtotal) {
            this.subtotal = subtotal;
        }
    }
}
