package com.belleza_app.inventario.application.usecase;

import com.belleza_app.inventario.domain.entities.Producto;
import com.belleza_app.inventario.domain.exceptions.ProductoNotFoundException;
import com.belleza_app.inventario.domain.ports.in.ConsultarStockEntrada;
import com.belleza_app.inventario.domain.ports.out.ProductoRepositorioPuerto;


public class ConsultarStockUseCase implements ConsultarStockEntrada {

    private final ProductoRepositorioPuerto repo;

    public ConsultarStockUseCase(ProductoRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public Producto ejecutar(String id) {
        return repo.buscarPorId(id)
            .orElseThrow(() -> new ProductoNotFoundException(id));
    }
}