package com.belleza_app.servicios.domain.exceptions;

public class ServicioNotFoundException extends RuntimeException {

    public ServicioNotFoundException(String id) {
        super("No se encontró el servicio con id: " + id);
    }
}