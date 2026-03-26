package com.belleza_app.servicios.infraestructure.adaptors.secondary.persistence;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.belleza_app.servicios.domain.entities.Servicio;

@Document(collection = "servicios")
public class ServicioDocument {

    @Id
    private String id;

    private String nombre;
    private double precio;
    private int duracionMinutos;
    private String estado;

    public static ServicioDocument fromDominio(Servicio s) {
        ServicioDocument d = new ServicioDocument();
        d.id               = s.getId();
        d.nombre           = s.getNombre();
        d.precio           = s.getPrecio();
        d.duracionMinutos  = s.getDuracionMinutos();
        d.estado           = s.getEstado().name();
        return d;
    }

    public Servicio toDominio() {
        Servicio s = new Servicio();
        s.setId(id);
        s.setNombre(nombre);
        s.setPrecio(precio);
        s.setDuracionMinutos(duracionMinutos);
        s.setEstado(Servicio.EstadoServicio.valueOf(estado));
        return s;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(int duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
