package com.belleza_app.citas.domain.entities;

import java.time.LocalDateTime;

import com.belleza_app.citas.domain.exceptions.FechaCitaInvalidaException;

import com.belleza_app.citas.domain.exceptions.CitaEstadoInvalidoException;

public class Cita {

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

    

    public Cita(String clienteNombre, String servicioId, String productoId, LocalDateTime fechaHora) {
        validarFechaFutura(fechaHora);
        this.clienteNombre = clienteNombre;
        this.servicioId    = servicioId;
        this.productoId    = productoId;
        this.fechaHora     = fechaHora;
        this.estado        = EstadoCita.PENDIENTE;
    }

    public void confirmar() {
        if (this.estado == EstadoCita.CANCELADA)
            throw new com.belleza_app.citas.domain.exceptions.CitaEstadoInvalidoException("No se puede confirmar una cita cancelada");
        this.estado = EstadoCita.CONFIRMADA;
    }

    public void cancelar() {
        if (this.estado == EstadoCita.CANCELADA)
            throw new CitaEstadoInvalidoException("La cita ya está cancelada");
        this.estado = EstadoCita.CANCELADA;
    }

    private void validarFechaFutura(LocalDateTime fecha) {
        if (fecha == null || !fecha.isAfter(LocalDateTime.now()))
            throw new FechaCitaInvalidaException("La fecha debe ser futura");
    }

    public Cita() {} 

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