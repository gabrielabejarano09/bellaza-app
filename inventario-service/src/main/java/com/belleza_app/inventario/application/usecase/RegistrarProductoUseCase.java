package com.belleza_app.inventario.application.usecase;

import com.belleza_app.inventario.domain.entities.Producto;
import com.belleza_app.inventario.domain.ports.in.RegistrarProductoEntrada;
import com.belleza_app.inventario.domain.ports.out.ProductoRepositorioPuerto;

public class RegistrarProductoUseCase implements RegistrarProductoEntrada {

    private final ProductoRepositorioPuerto repo;

    public RegistrarProductoUseCase(ProductoRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public Producto ejecutar(Producto producto) {
        return repo.guardar(producto);
    }
}