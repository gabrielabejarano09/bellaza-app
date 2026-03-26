package com.belleza_app.inventario.application.usecase;


import com.belleza_app.inventario.domain.ports.in.DescontarStockEntrada;
import com.belleza_app.inventario.domain.ports.out.ProductoRepositorioPuerto;
import com.belleza_app.inventario.domain.entities.Producto;
import com.belleza_app.inventario.domain.exceptions.ProductoNotFoundException;
import com.belleza_app.inventario.domain.exceptions.StockInsuficienteException;

public class DescontarStockUseCase implements DescontarStockEntrada {
    private final ProductoRepositorioPuerto repo;
    public DescontarStockUseCase(ProductoRepositorioPuerto repo) { this.repo = repo; }

    @Override
    public Producto ejecutar(String id, int cantidad) {
        if (cantidad <= 0)
            throw new StockInsuficienteException("La cantidad debe ser mayor a cero");
        Producto p = repo.buscarPorId(id).orElseThrow(() -> new ProductoNotFoundException(id));
        if (cantidad > p.getStock())
            throw new StockInsuficienteException(id, cantidad, p.getStock());
        p.setStock(p.getStock() - cantidad);
        return repo.guardar(p);
    }
}
