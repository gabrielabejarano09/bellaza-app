package com.belleza_app.servicios.application.usecase;

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.exceptions.ServicioNotFoundException;
import com.belleza_app.servicios.domain.ports.in.ActivarServicioEntrada;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;

public class ActivarServicioUseCase implements ActivarServicioEntrada {

    private final ServicioRepositorioPuerto repo;

    public ActivarServicioUseCase(ServicioRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public Servicio ejecutar(String id) {
        Servicio servicio = repo.buscarPorId(id)
            .orElseThrow(() -> new ServicioNotFoundException(id));

        servicio.setEstado(Servicio.EstadoServicio.ACTIVO);

        return repo.guardar(servicio);
    }
}