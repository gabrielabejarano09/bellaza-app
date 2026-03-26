package com.belleza_app.citas.domain.ports.in;

import com.belleza_app.citas.domain.entities.Cita;

public interface ConsultarCitaEntrada { Cita ejecutar(String citaId); }