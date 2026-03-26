package com.belleza_app.citas.domain.ports.in;

import com.belleza_app.citas.domain.entities.Cita;

public interface AgendarCitaEntrada {
    Cita ejecutar(String clienteNombre, String servicioId,
                  String productoId, java.time.LocalDateTime fechaHora);
}
