package com.belleza_app.servicios.application.usecase;

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.exceptions.ServicioNotFoundException;
import com.belleza_app.servicios.domain.ports.in.BuscarServicioEntrada;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;

public class BuscarServicioUseCase implements BuscarServicioEntrada {

    private final ServicioRepositorioPuerto repo;

    public BuscarServicioUseCase(ServicioRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public Servicio ejecutar(String id) {
        return repo.buscarPorId(id)
            .orElseThrow(() -> new ServicioNotFoundException(id));
    }
}