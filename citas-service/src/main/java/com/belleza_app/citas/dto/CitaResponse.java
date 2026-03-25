package com.belleza_app.citas.dto;

import com.belleza_app.citas.model.Cita;

import java.time.LocalDateTime;

public class CitaResponse {

    private String id;
    private String clienteNombre;
    private String servicioId;
    private String productoId;
    private LocalDateTime fechaHora;
    private Cita.EstadoCita estado;

    public CitaResponse() {
    }

    public CitaResponse(String id, String clienteNombre, String servicioId,
                        String productoId, LocalDateTime fechaHora,
                        Cita.EstadoCita estado) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.servicioId = servicioId;
        this.productoId = productoId;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public static CitaResponse fromModel(Cita cita) {
        return new CitaResponse(
                cita.getId(),
                cita.getClienteNombre(),
                cita.getServicioId(),
                cita.getProductoId(),
                cita.getFechaHora(),
                cita.getEstado()
        );
    }

    public String getId() {
        return id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public String getServicioId() {
        return servicioId;
    }

    public String getProductoId() {
        return productoId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Cita.EstadoCita getEstado() {
        return estado;
    }
}