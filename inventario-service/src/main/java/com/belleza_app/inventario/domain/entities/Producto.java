package com.belleza_app.inventario.domain.entities;


public class Producto {

    
    private String id;
    private String nombre;
    private String descripcion;
    private int stock;

    public Producto() {
    }

    public Producto(String nombre, String descripcion, int stock) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.stock = stock;
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

    public int getStock() {
        return stock;
    }

    public void setStock( int stock) {
        this.stock = stock;
    }
}