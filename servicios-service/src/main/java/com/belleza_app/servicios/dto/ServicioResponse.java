package com.belleza_app.servicios.dto;

import com.belleza_app.servicios.model.Servicio;

public class ServicioResponse {

    private String id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int duracionMinutos;
    private Servicio.EstadoServicio estado;

    public ServicioResponse() {
    }

    public ServicioResponse(String id, String nombre, String descripcion,
                            double precio, int duracionMinutos,
                            Servicio.EstadoServicio estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
        this.estado = estado;
    }

    public static ServicioResponse fromEntity(Servicio servicio) {
        return new ServicioResponse(
                servicio.getId(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.getPrecio(),
                servicio.getDuracionMinutos(),
                servicio.getEstado()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Servicio.EstadoServicio getEstado() {
        return estado;
    }

    public void setEstado(Servicio.EstadoServicio estado) {
        this.estado = estado;
    }
}