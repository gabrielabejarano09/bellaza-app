package com.belleza_app.inventario.service;

import com.belleza_app.inventario.dto.StockResponse;
import com.belleza_app.inventario.exception.ProductoNotFoundException;
import com.belleza_app.inventario.exception.StockInsuficienteException;
import com.belleza_app.inventario.model.Producto;
import com.belleza_app.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public Producto registrarProducto(Producto producto) {
        if (producto.getStock() < 0) {
            throw new StockInsuficienteException("El stock inicial no puede ser negativo");
        }
        return repository.save(producto);
    }

    public Producto buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
    }

    public StockResponse consultarStock(String id) {
        Producto producto = buscarPorId(id);
        return new StockResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getStock(),
                producto.getStock() > 0
        );
    }

    public Producto agregarStock(String id, int cantidad) {
        if (cantidad <= 0) {
            throw new StockInsuficienteException("La cantidad a agregar debe ser mayor a cero");
        }

        Producto producto = buscarPorId(id);
        producto.setStock(producto.getStock() + cantidad);
        return repository.save(producto);
    }

    public Producto descontarStock(String id, int cantidad) {
        if (cantidad <= 0) {
            throw new StockInsuficienteException("La cantidad a descontar debe ser mayor a cero");
        }

        Producto producto = buscarPorId(id);

        if (cantidad > producto.getStock()) {
            throw new StockInsuficienteException(
                    "La cantidad a descontar no puede superar el stock disponible. Stock actual: "
                            + producto.getStock() + ", cantidad solicitada: " + cantidad
            );
        }

        producto.setStock(producto.getStock() - cantidad);
        return repository.save(producto);
    }

    public boolean hayStockDisponible(String id, int cantidad) {
        Producto producto = buscarPorId(id);
        return producto.getStock() >= cantidad;
    }
}