package com.belleza_app.citas.application.usecase;

import com.belleza_app.citas.domain.entities.Cita;
import com.belleza_app.citas.domain.exceptions.CitaNotFoundException;
import com.belleza_app.citas.domain.ports.in.ConsultarCitaEntrada;
import com.belleza_app.citas.domain.ports.out.CitaRepositorioPuerto;

public class ConsultarCitaUseCase implements ConsultarCitaEntrada {

    private final CitaRepositorioPuerto repo;

    public ConsultarCitaUseCase(CitaRepositorioPuerto repo) {
        this.repo = repo;
    }

    @Override
    public Cita ejecutar(String id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new CitaNotFoundException(id));
    }
}