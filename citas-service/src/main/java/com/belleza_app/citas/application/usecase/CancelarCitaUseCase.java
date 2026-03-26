package com.belleza_app.citas.application.usecase;

import com.belleza_app.citas.domain.entities.Cita;
import com.belleza_app.citas.domain.exceptions.CitaNotFoundException;
import com.belleza_app.citas.domain.ports.in.CancelarCitaEntrada;
import com.belleza_app.citas.domain.ports.out.CitaRepositorioPuerto;

public class CancelarCitaUseCase implements CancelarCitaEntrada {
    private final CitaRepositorioPuerto citaRepo;
    public CancelarCitaUseCase(CitaRepositorioPuerto citaRepo) { this.citaRepo = citaRepo; }

    @Override
    public Cita ejecutar(String citaId) {
        Cita cita = citaRepo.buscarPorId(citaId)
            .orElseThrow(() -> new CitaNotFoundException(citaId));
        cita.cancelar();
        return citaRepo.guardar(cita);
    }
}
