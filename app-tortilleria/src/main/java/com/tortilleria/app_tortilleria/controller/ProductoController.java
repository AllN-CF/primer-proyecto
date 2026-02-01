package com.tortilleria.app_tortilleria.controller;

import com.tortilleria.app_tortilleria.model.Producto;
import com.tortilleria.app_tortilleria.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> nuevoProducto(@RequestBody Producto producto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.agregarProducto(producto));

    }

    @GetMapping("/lista")
    public  ResponseEntity<List<Producto>> verListaProductos() {

        return ResponseEntity.status(HttpStatus.OK).body(productoService.listarProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> verProducto(@PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK).body(productoService.obtenerProducto(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {

        productoService.quitarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {

        return ResponseEntity.status(HttpStatus.OK).body(productoService.modificarProducto(id, producto));

    }
}
