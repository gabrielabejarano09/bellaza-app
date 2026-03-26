package com.belleza_app.servicios.application.usecase;

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.ports.in.CrearServicioEntrada;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;

public class CrearServicioUseCase implements CrearServicioEntrada {
    private final ServicioRepositorioPuerto repo;
    public CrearServicioUseCase(ServicioRepositorioPuerto repo) { this.repo = repo; }

    @Override
    public Servicio ejecutar(Servicio servicio) {
        servicio.validarPrecio(servicio.getPrecio());
        servicio.validarDuracion(servicio.getDuracionMinutos());
        servicio.setEstado(Servicio.EstadoServicio.ACTIVO);
        return repo.guardar(servicio);
    }
}
