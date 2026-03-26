package com.belleza_app.inventario.infraestructure.adaptors.secondary.persistence;

import org.springframework.stereotype.Component;

import com.belleza_app.inventario.domain.entities.Producto;
import com.belleza_app.inventario.domain.ports.out.ProductoRepositorioPuerto;

import java.util.Optional;

@Component
public class ProductoRepositorioAdapter implements ProductoRepositorioPuerto {

    private final ProductoMongoRepository mongoRepo;

    public ProductoRepositorioAdapter(ProductoMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public Producto guardar(Producto producto) {
        return mongoRepo.save(ProductoDocument.fromDominio(producto)).toDominio();
    }

    @Override
    public Optional<Producto> buscarPorId(String id) {
        return mongoRepo.findById(id).map(ProductoDocument::toDominio);
    }
}