package com.belleza_app.inventario.infraestructure.adaptors.secondary.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductoMongoRepository extends MongoRepository<ProductoDocument, String> {
}