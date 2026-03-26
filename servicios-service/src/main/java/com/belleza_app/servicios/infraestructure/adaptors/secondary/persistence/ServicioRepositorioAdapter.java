package com.belleza_app.servicios.infraestructure.adaptors.secondary.persistence;

import org.springframework.stereotype.Component;

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ServicioRepositorioAdapter implements ServicioRepositorioPuerto {

    private final ServicioMongoRepository mongoRepo;

    public ServicioRepositorioAdapter(ServicioMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        return mongoRepo.save(ServicioDocument.fromDominio(servicio)).toDominio();
    }

    @Override
    public Optional<Servicio> buscarPorId(String id) {
        return mongoRepo.findById(id).map(ServicioDocument::toDominio);
    }

    @Override
    public List<Servicio> buscarActivos() {
        return mongoRepo.findByEstado("ACTIVO")
                .stream()
                .map(ServicioDocument::toDominio)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(String id) {
        mongoRepo.deleteById(id);
    }

    @Override
    public boolean existeActivo(String id) {
        return mongoRepo.findById(id)
                .map(s -> s.getEstado().equals("ACTIVO"))
                .orElse(false);
    }
}