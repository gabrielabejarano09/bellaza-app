package com.belleza_app.citas.infraestructure.adaptors.primary.rest;


import com.belleza_app.citas.domain.entities.Cita;
import com.belleza_app.citas.domain.ports.in.AgendarCitaEntrada;
import com.belleza_app.citas.domain.ports.in.CancelarCitaEntrada;
import com.belleza_app.citas.domain.ports.in.ConfirmarCitaEntrada;
import com.belleza_app.citas.domain.ports.in.ConsultarCitaEntrada;
import com.belleza_app.citas.infraestructure.adaptors.primary.rest.dto.CitaRequest;
import com.belleza_app.citas.infraestructure.adaptors.primary.rest.dto.CitaResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final AgendarCitaEntrada   agendarCita;
    private final ConfirmarCitaEntrada  confirmarCita;
    private final CancelarCitaEntrada   cancelarCita;
    private final ConsultarCitaEntrada  consultarCita;


    public CitaController(AgendarCitaEntrada agendarCita,
                          ConfirmarCitaEntrada confirmarCita,
                          CancelarCitaEntrada cancelarCita,
                          ConsultarCitaEntrada consultarCita) {
        this.agendarCita   = agendarCita;
        this.confirmarCita = confirmarCita;
        this.cancelarCita  = cancelarCita;
        this.consultarCita = consultarCita;
    }

    @PostMapping
    public ResponseEntity<CitaResponse> crear(@Valid @RequestBody CitaRequest req) {
        Cita cita = agendarCita.ejecutar(req.getClienteNombre(), req.getServicioId(),
                                         req.getProductoId(), req.getFechaHora());
        return ResponseEntity.status(201).body(CitaResponse.fromDominio(cita));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<CitaResponse> confirmar(@PathVariable String id) {
        return ResponseEntity.ok(CitaResponse.fromDominio(confirmarCita.ejecutar(id)));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponse> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(CitaResponse.fromDominio(cancelarCita.ejecutar(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponse> buscar(@PathVariable String id) {
        return ResponseEntity.ok(CitaResponse.fromDominio(consultarCita.ejecutar(id)));
    }
}

