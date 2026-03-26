package com.belleza_app.servicios.infraestructure.adaptors.secondary.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ServicioMongoRepository extends MongoRepository<ServicioDocument, String> {

    List<ServicioDocument> findByEstado(String estado);
}