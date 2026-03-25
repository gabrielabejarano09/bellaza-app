package com.belleza_app.inventario.exception;

public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }

    public StockInsuficienteException(String id, int solicitado, int disponible) {
        super("Stock insuficiente para el producto con id: " + id
                + ". Solicitado: " + solicitado
                + ", disponible: " + disponible);
    }
}