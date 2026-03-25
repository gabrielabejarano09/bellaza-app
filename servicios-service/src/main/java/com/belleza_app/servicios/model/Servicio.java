package com.belleza_app.servicios.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB que representa un servicio de la empresa de belleza.
 * Colección: "servicios"
 */
@Document(collection = "servicios")
public class Servicio {

    @Id
    private String id;

    @NotBlank(message = "El nombre del servicio no puede estar vacío")
    private String nombre;

    private String descripcion;

    /**
     * Regla de negocio: el precio no puede ser cero ni negativo.
     */
    @Positive(message = "El precio debe ser mayor a cero")
    private double precio;

    /**
     * Regla de negocio: la duración debe estar entre 15 y 480 minutos.
     * La validación del máximo se hace en la capa de servicio.
     */
    @Min(value = 15, message = "La duración mínima es de 15 minutos")
    private int duracionMinutos;

    /**
     * Estado del servicio: ACTIVO o INACTIVO.
     * Una cita no puede confirmarse si el servicio está INACTIVO.
     */
    private EstadoServicio estado;

    public enum EstadoServicio {
        ACTIVO, INACTIVO
    }

    // ─── Constructores ───────────────────────────────────────────────────────

    public Servicio() {}

    public Servicio(String nombre, String descripcion, double precio, int duracionMinutos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
        this.estado = EstadoServicio.ACTIVO; // por defecto, activo al crearlo
    }

    // ─── Getters y Setters ───────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public EstadoServicio getEstado() { return estado; }
    public void setEstado(EstadoServicio estado) { this.estado = estado; }
}