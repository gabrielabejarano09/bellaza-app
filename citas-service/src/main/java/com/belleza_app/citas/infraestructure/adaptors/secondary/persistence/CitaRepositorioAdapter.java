package com.belleza_app.citas.infraestructure.adaptors.secondary.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.belleza_app.citas.domain.entities.Cita;
import com.belleza_app.citas.domain.ports.out.CitaRepositorioPuerto;

@Component 
public class CitaRepositorioAdapter implements CitaRepositorioPuerto {
    private final CitaMongoRepository mongoRepo;
    public CitaRepositorioAdapter(CitaMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }

    @Override public Cita guardar(Cita c) {
        return mongoRepo.save(CitaDocument.fromDominio(c)).toDominio(); }

    @Override public Optional<Cita> buscarPorId(String id) {
        return mongoRepo.findById(id).map(CitaDocument::toDominio); }

    @Override public List<Cita> buscarPorCliente(String nombre) {
        return mongoRepo.findByClienteNombre(nombre)
            .stream().map(CitaDocument::toDominio).collect(Collectors.toList()); }

    @Override public boolean existeConflictoHorario(String nombre, LocalDateTime fecha) {
        return mongoRepo.findByClienteNombreAndFechaHora(nombre, fecha).isPresent(); }
}
