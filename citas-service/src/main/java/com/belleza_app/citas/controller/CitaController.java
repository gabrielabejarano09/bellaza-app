package com.belleza_app.citas.controller;

import com.belleza_app.citas.dto.CitaRequest;
import com.belleza_app.citas.model.Cita;
import com.belleza_app.citas.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @PostMapping
    public ResponseEntity<Cita> crearCita(@Valid @RequestBody CitaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(citaService.crearCita(request));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<Cita> confirmar(@PathVariable String id) {
        return ResponseEntity.ok(citaService.confirmarCita(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Cita> cancelar(@PathVariable String id) {
        return ResponseEntity.ok(citaService.cancelarCita(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(citaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteNombre}")
    public ResponseEntity<List<Cita>> listarPorCliente(@PathVariable String clienteNombre) {
        return ResponseEntity.ok(citaService.listarPorCliente(clienteNombre));
    }
}