package com.belleza_app.citas.application.usecase;


import java.time.LocalDateTime;

import com.belleza_app.citas.domain.entities.Cita;
import com.belleza_app.citas.domain.exceptions.CitaConflictoException;
import com.belleza_app.citas.domain.exceptions.ServicioInactivoException;
import com.belleza_app.citas.domain.ports.in.AgendarCitaEntrada;
import com.belleza_app.citas.domain.ports.out.CitaRepositorioPuerto;
import com.belleza_app.citas.domain.ports.out.ServicioClientePuerto;

public class AgendarCitaUseCase implements AgendarCitaEntrada {

    private final CitaRepositorioPuerto citaRepo;
    private final ServicioClientePuerto servicioCliente;

    public AgendarCitaUseCase(CitaRepositorioPuerto citaRepo,
                              ServicioClientePuerto servicioCliente) {
        this.citaRepo        = citaRepo;
        this.servicioCliente = servicioCliente;
    }

    @Override
    public Cita ejecutar(String clienteNombre, String servicioId,
                         String productoId, LocalDateTime fechaHora) {
        if (!servicioCliente.esServicioActivo(servicioId))
            throw new ServicioInactivoException(servicioId);
        if (citaRepo.existeConflictoHorario(clienteNombre, fechaHora))
            throw new CitaConflictoException("El cliente ya tiene cita en ese horario");
        Cita cita = new Cita(clienteNombre, servicioId, productoId, fechaHora);

        return citaRepo.guardar(cita);
    }
}
