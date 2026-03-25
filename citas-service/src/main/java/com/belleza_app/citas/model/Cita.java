package com.belleza_app.citas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "citas")
public class Cita {

    @Id
    private String id;

    private String clienteNombre;
    private String servicioId;
    private String productoId;
    private LocalDateTime fechaHora;
    private EstadoCita estado;

    public enum EstadoCita {
        PENDIENTE,
        CONFIRMADA,
        CANCELADA
    }

    public Cita() {
    }

    public Cita(String clienteNombre, String servicioId, String productoId,
                LocalDateTime fechaHora, EstadoCita estado) {
        this.clienteNombre = clienteNombre;
        this.servicioId = servicioId;
        this.productoId = productoId;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getServicioId() {
        return servicioId;
    }

    public void setServicioId(String servicioId) {
        this.servicioId = servicioId;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }
}