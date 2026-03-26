package com.belleza_app.citas.infraestructure.adaptors.secondary.persistence;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.belleza_app.citas.domain.entities.Cita;

import java.time.LocalDateTime;

@Document(collection = "citas")  

public class CitaDocument {
    @Id private String id;
    private String clienteNombre, servicioId, productoId, estado;
    private LocalDateTime fechaHora;

    public static CitaDocument fromDominio(Cita cita) {
        CitaDocument d = new CitaDocument();
        d.id            = cita.getId();
        d.clienteNombre = cita.getClienteNombre();
        d.servicioId    = cita.getServicioId();
        d.productoId    = cita.getProductoId();
        d.fechaHora     = cita.getFechaHora();
        d.estado        = cita.getEstado().name();
        return d;
    }

    public Cita toDominio() {
        Cita c = new Cita();
        c.setId(id); c.setClienteNombre(clienteNombre);
        c.setServicioId(servicioId); c.setProductoId(productoId);
        c.setFechaHora(fechaHora);
        c.setEstado(Cita.EstadoCita.valueOf(estado));
        return c;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
