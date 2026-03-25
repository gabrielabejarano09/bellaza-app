package com.belleza_app.servicios.repository;

import com.belleza_app.servicios.model.Servicio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ServicioRepository extends MongoRepository<Servicio, String> {

    List<Servicio> findByEstado(Servicio.EstadoServicio estado);
}