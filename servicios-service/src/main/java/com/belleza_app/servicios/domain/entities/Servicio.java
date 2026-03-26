package com.belleza_app.servicios.domain.entities;


import com.belleza_app.servicios.domain.exceptions.ReglaNegocioServicioException;


public class Servicio {

    private String id;
    private String nombre;
    private String descripcion;

    private double precio;
    private int duracionMinutos;
    private EstadoServicio estado;

    public enum EstadoServicio {
        ACTIVO, INACTIVO
    }

    public Servicio() {}

    public Servicio(String nombre, String descripcion, double precio, int duracionMinutos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
        this.estado = EstadoServicio.ACTIVO; 
    }

    public void validarPrecio(double precio) {
    if (precio <= 0)
        throw new ReglaNegocioServicioException("El precio debe ser mayor a cero");
}

    public void validarDuracion(int minutos) {
        if (minutos < 15 || minutos > 480)
            throw new ReglaNegocioServicioException("Duracion entre 15 y 480 minutos");
    }


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