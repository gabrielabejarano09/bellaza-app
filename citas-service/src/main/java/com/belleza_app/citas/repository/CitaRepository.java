package com.belleza_app.citas.repository;

import com.belleza_app.citas.model.Cita;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CitaRepository extends MongoRepository<Cita, String> {

    Optional<Cita> findByClienteNombreAndFechaHora(String clienteNombre, LocalDateTime fechaHora);

    List<Cita> findByClienteNombre(String clienteNombre);
}