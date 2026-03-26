package com.belleza_app.citas.domain.ports.out;

import com.belleza_app.citas.domain.entities.Cita;
import java.time.LocalDateTime;
import java.util.List; import java.util.Optional;

public interface CitaRepositorioPuerto {
    Cita guardar(Cita cita);
    Optional<Cita> buscarPorId(String id);
    List<Cita> buscarPorCliente(String clienteNombre);
    boolean existeConflictoHorario(String clienteNombre, LocalDateTime fechaHora);
}



