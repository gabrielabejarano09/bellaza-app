package com.belleza_app.servicios.infraestructure.adaptors.primary.rest;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.ports.in.ActivarServicioEntrada;
import com.belleza_app.servicios.domain.ports.in.BuscarServicioEntrada;
import com.belleza_app.servicios.domain.ports.in.CrearServicioEntrada;
import com.belleza_app.servicios.domain.ports.in.DesactivarServicioEntrada;
import com.belleza_app.servicios.domain.ports.in.EsServicioActivoEntrada;
import com.belleza_app.servicios.domain.ports.in.ListarServiciosActivosEntrada;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final CrearServicioEntrada crearServicio;
    private final BuscarServicioEntrada buscarServicio;
    private final ListarServiciosActivosEntrada listarActivos;
    private final EsServicioActivoEntrada esServicioActivo;
    private final ActivarServicioEntrada activarServicio;
    private final DesactivarServicioEntrada desactivarServicio;

    public ServicioController(
            CrearServicioEntrada crearServicio,
            BuscarServicioEntrada buscarServicio,
            ListarServiciosActivosEntrada listarActivos,
            EsServicioActivoEntrada esServicioActivo,
            ActivarServicioEntrada activarServicio,
            DesactivarServicioEntrada desactivarServicio
    ) {
        this.crearServicio = crearServicio;
        this.buscarServicio = buscarServicio;
        this.listarActivos = listarActivos;
        this.esServicioActivo = esServicioActivo;
        this.activarServicio = activarServicio;
        this.desactivarServicio = desactivarServicio;
    }

    @PostMapping
    public ResponseEntity<Servicio> crearServicio(@Valid @RequestBody Servicio servicio) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(crearServicio.ejecutar(servicio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(buscarServicio.ejecutar(id));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Servicio>> listarActivos() {
        return ResponseEntity.ok(listarActivos.ejecutar());
    }

    @GetMapping("/{id}/activo")
    public ResponseEntity<Boolean> esActivo(@PathVariable String id) {
        return ResponseEntity.ok(esServicioActivo.ejecutar(id));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Servicio> activar(@PathVariable String id) {
        return ResponseEntity.ok(activarServicio.ejecutar(id));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Servicio> desactivar(@PathVariable String id) {
        return ResponseEntity.ok(desactivarServicio.ejecutar(id));
    }
}