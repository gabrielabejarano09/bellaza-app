package com.belleza_app.servicios.application.usecase;

import java.util.List;

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.ports.in.ListarServiciosActivosEntrada;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;

public class ListarServiciosActivosUseCase implements ListarServiciosActivosEntrada {

    private final ServicioRepositorioPuerto repo;

    public ListarServiciosActivosUseCase(ServicioRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public List<Servicio> ejecutar() {
        return repo.buscarActivos();
    }
}