package com.tortilleria.app_tortilleria.dto;

import java.util.List;

public class PedidoRequestDTO {
    private List<ProductoPedidoRequest> productos;

    public List<ProductoPedidoRequest> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoPedidoRequest> productos) {
        this.productos = productos;
    }

    public static class ProductoPedidoRequest {
        private Long idProducto;
        private int cantidad;

        public Long getIdProducto() {
            return idProducto;
        }

        public void setIdProducto(Long idProducto) {
            this.idProducto = idProducto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }
}
