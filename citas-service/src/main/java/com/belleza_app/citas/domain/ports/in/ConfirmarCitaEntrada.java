package com.belleza_app.citas.domain.ports.in;

import com.belleza_app.citas.domain.entities.Cita;

public interface ConfirmarCitaEntrada { Cita ejecutar(String citaId); }