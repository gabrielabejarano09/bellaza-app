package com.belleza_app.inventario.domain.ports.out;

import java.util.Optional;

import com.belleza_app.inventario.domain.entities.Producto;

public interface ProductoRepositorioPuerto {
    Producto guardar(Producto producto);
    Optional<Producto> buscarPorId(String id);
}
