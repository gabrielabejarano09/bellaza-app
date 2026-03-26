package com.belleza_app.servicios.application.usecase;

import com.belleza_app.servicios.domain.ports.in.EsServicioActivoEntrada;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;

public class EsServicioActivoUseCase implements EsServicioActivoEntrada {

    private final ServicioRepositorioPuerto repo;

    public EsServicioActivoUseCase(ServicioRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public boolean ejecutar(String id) {
        return repo.existeActivo(id);
    }
}