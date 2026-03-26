package com.belleza_app.citas.infraestructure.adaptors.secondary.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CitaMongoRepository extends MongoRepository<CitaDocument, String> {
    Optional<CitaDocument> findByClienteNombreAndFechaHora(
        String clienteNombre, LocalDateTime fechaHora);
    List<CitaDocument> findByClienteNombre(String clienteNombre);
}
