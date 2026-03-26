package com.belleza_app.inventario.application.usecase;

import com.belleza_app.inventario.domain.ports.in.HayStockDisponibleEntrada;
import com.belleza_app.inventario.domain.ports.out.ProductoRepositorioPuerto;

public class HayStockDisponibleUseCase implements HayStockDisponibleEntrada {

    private final ProductoRepositorioPuerto repo;

    public HayStockDisponibleUseCase(ProductoRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public boolean ejecutar(String id, int cantidad) {
        return repo.buscarPorId(id)
            .map(p -> p.getStock() >= cantidad)
            .orElse(false);
    }
}