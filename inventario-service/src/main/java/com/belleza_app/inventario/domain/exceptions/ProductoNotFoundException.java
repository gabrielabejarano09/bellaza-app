package com.belleza_app.inventario.domain.exceptions;

public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(String id) {
        super("No se encontró el producto con id: " + id);
    }
}