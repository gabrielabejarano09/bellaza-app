package com.belleza_app.servicios.controller;

import com.belleza_app.servicios.model.Servicio;
import com.belleza_app.servicios.service.ServicioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST del módulo de Servicios.
 * Expone endpoints en el puerto 8082.
 *
 * El módulo de Citas consumirá GET /api/servicios/{id}/activo
 * para verificar si un servicio está activo antes de confirmar una cita.
 */
@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    /**
     * POST /api/servicios
     * Caso de uso: Crear servicio.
     * Body: { "nombre": "Corte", "precio": 25000, "duracionMinutos": 60 }
     */
    @PostMapping
    public ResponseEntity<Servicio> crearServicio(@Valid @RequestBody Servicio servicio) {
        Servicio creado = servicioService.crearServicio(servicio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * GET /api/servicios/{id}
     * Caso de uso: Consultar servicio por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Servicio> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(servicioService.buscarPorId(id));
    }

    /**
     * GET /api/servicios/activos
     * Caso de uso: Listar servicios activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<List<Servicio>> listarActivos() {
        return ResponseEntity.ok(servicioService.listarActivos());
    }

    /**
     * GET /api/servicios/{id}/activo
     * Endpoint auxiliar consumido por citas-service vía REST.
     * Retorna true/false indicando si el servicio está ACTIVO.
     */
    @GetMapping("/{id}/activo")
    public ResponseEntity<Boolean> esActivo(@PathVariable String id) {
        return ResponseEntity.ok(servicioService.esServicioActivo(id));
    }

   
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Servicio> activar(@PathVariable String id) {
        return ResponseEntity.ok(servicioService.activarServicio(id));
    }

    /**
     * PATCH /api/servicios/{id}/desactivar
     * Caso de uso: Desactivar servicio.
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Servicio> desactivar(@PathVariable String id) {
        return ResponseEntity.ok(servicioService.desactivarServicio(id));
    }

    /**
     * DELETE /api/servicios/{id}
     * Caso de uso: Eliminar servicio (solo si no tiene citas futuras).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        servicioService.eliminarServicio(id);
        return ResponseEntity.noContent().build();
    }
}