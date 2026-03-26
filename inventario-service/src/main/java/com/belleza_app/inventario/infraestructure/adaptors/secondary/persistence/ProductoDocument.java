package com.belleza_app.inventario.infraestructure.adaptors.secondary.persistence;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.belleza_app.inventario.domain.entities.Producto;

@Document(collection = "productos")
public class ProductoDocument {

    @Id
    private String id;

    private String nombre;
    private int stock;

    public static ProductoDocument fromDominio(Producto p) {
        ProductoDocument d = new ProductoDocument();
        d.id     = p.getId();
        d.nombre = p.getNombre();
        d.stock  = p.getStock();
        return d;
    }

    public Producto toDominio() {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setStock(stock);
        return p;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}