package com.belleza_app.inventario.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "productos")
public class Producto {

    @Id
    private String id;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombre;

    private String descripcion;

    @Min(value = 0, message = "El stock no puede ser negativo")
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

    public @NotBlank(message = "El nombre del producto no puede estar vacío") String getNombre() {
        return nombre;
    }

    public void setNombre(@NotBlank(message = "El nombre del producto no puede estar vacío") String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public @Min(value = 0, message = "El stock no puede ser negativo") int getStock() {
        return stock;
    }

    public void setStock(@Min(value = 0, message = "El stock no puede ser negativo") int stock) {
        this.stock = stock;
    }
}