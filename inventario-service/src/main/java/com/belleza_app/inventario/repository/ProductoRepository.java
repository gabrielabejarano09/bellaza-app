package com.belleza_app.inventario.repository;

import com.belleza_app.inventario.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductoRepository extends MongoRepository<Producto, String> {
}