package com.tortilleria.app_tortilleria.service;

import com.tortilleria.app_tortilleria.exception.RecursoNoEncontradoException;
import com.tortilleria.app_tortilleria.model.Producto;
import com.tortilleria.app_tortilleria.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto agregarProducto(Producto producto) {
        if(producto.getId() != null && productoRepository.existsById(producto.getId()))
            throw new IllegalArgumentException("El producto ya cuenta con un ID.");

        if(producto.getNombre() == null || producto.getPrecio() == 0)
            throw new IllegalArgumentException("Todos los campos deben ser llenados correctamente.");

        return productoRepository.save(producto);
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProducto(Long id) {
        Optional<Producto> productoBuscado = productoRepository.findById(id);

        if(productoBuscado.isPresent())  return productoBuscado.get();
        else throw new RecursoNoEncontradoException("El producto con ID " + id + " no existe.");
    }

    public void quitarProducto(Long id) {
        if(!productoRepository.existsById(id))
            throw new RecursoNoEncontradoException("El producto con ID " + id + " no existe.");

        productoRepository.deleteById(id);
    }

    public Producto modificarProducto(Long id, Producto producto) {
        Optional<Producto> productoActual = productoRepository.findById(id);

        if(productoActual.isPresent()) {
             Producto productoTemporal = productoActual.get();

            if (producto.getNombre() == null || producto.getPrecio() == 0)
                throw new IllegalArgumentException("Todos los campos deben ser llenados correctamente.");

            productoTemporal.setNombre(producto.getNombre());
            productoTemporal.setPrecio(producto.getPrecio());
            productoTemporal.setDisponibilidad(producto.getDisponibilidad());

            return productoRepository.save(productoTemporal);
        }
        else throw new RecursoNoEncontradoException("El producto con ID " + id + " no existe.");
    }
}
