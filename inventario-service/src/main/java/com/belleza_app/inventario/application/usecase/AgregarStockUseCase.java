package com.belleza_app.inventario.application.usecase;

import com.belleza_app.inventario.domain.entities.Producto;
import com.belleza_app.inventario.domain.exceptions.ProductoNotFoundException;
import com.belleza_app.inventario.domain.ports.in.AgregarStockEntrada;
import com.belleza_app.inventario.domain.ports.out.ProductoRepositorioPuerto;

public class AgregarStockUseCase implements AgregarStockEntrada {

    private final ProductoRepositorioPuerto repo;

    public AgregarStockUseCase(ProductoRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public Producto ejecutar(String id, int cantidad) {
        Producto producto = repo.buscarPorId(id)
            .orElseThrow(() -> new ProductoNotFoundException(id));

        producto.setStock(producto.getStock() + cantidad);

        return repo.guardar(producto);
    }
}