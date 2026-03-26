package com.belleza_app.servicios.application.usecase;

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.exceptions.ServicioNotFoundException;
import com.belleza_app.servicios.domain.ports.in.DesactivarServicioEntrada;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;

public class DesactivarServicioUseCase implements DesactivarServicioEntrada {

    private final ServicioRepositorioPuerto repo;

    public DesactivarServicioUseCase(ServicioRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public Servicio ejecutar(String id) {
        Servicio servicio = repo.buscarPorId(id)
            .orElseThrow(() -> new ServicioNotFoundException(id));

        servicio.setEstado(Servicio.EstadoServicio.INACTIVO);

        return repo.guardar(servicio);
    }
}