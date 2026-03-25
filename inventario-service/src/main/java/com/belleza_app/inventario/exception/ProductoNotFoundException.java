package com.belleza_app.inventario.exception;

public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(String id) {
        super("No se encontró el producto con id: " + id);
    }
}