package com.belleza_app.servicios.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.belleza_app.servicios.domain.entities.Servicio;

public interface ServicioRepositorioPuerto {
    Servicio guardar(Servicio servicio);
    Optional<Servicio> buscarPorId(String id);
    List<Servicio> buscarActivos();
    void eliminar(String id);
    boolean existeActivo(String id);
}

