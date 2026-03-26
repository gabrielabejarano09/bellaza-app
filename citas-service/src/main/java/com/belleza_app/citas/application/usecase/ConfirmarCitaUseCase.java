package com.belleza_app.citas.application.usecase;

import com.belleza_app.citas.domain.entities.Cita;
import com.belleza_app.citas.domain.exceptions.CitaNotFoundException;
import com.belleza_app.citas.domain.exceptions.ReglaNegocioCitaException;
import com.belleza_app.citas.domain.ports.in.ConfirmarCitaEntrada;
import com.belleza_app.citas.domain.ports.out.CitaRepositorioPuerto;
import com.belleza_app.citas.domain.ports.out.InventarioClientePuerto;

public class ConfirmarCitaUseCase implements ConfirmarCitaEntrada {
    private final CitaRepositorioPuerto citaRepo;
    private final InventarioClientePuerto inventarioCliente;

    public ConfirmarCitaUseCase(CitaRepositorioPuerto citaRepo,
                                InventarioClientePuerto inventarioCliente) {
        this.citaRepo          = citaRepo;
        this.inventarioCliente = inventarioCliente;
    }

    @Override
    public Cita ejecutar(String citaId) {
        Cita cita = citaRepo.buscarPorId(citaId)
            .orElseThrow(() -> new CitaNotFoundException(citaId));
        if (!inventarioCliente.hayStockDisponible(cita.getProductoId(), 1))
            throw new ReglaNegocioCitaException("No hay stock suficiente");
        cita.confirmar();  
        return citaRepo.guardar(cita);
    }
}


