package com.belleza_app.citas.domain.exceptions;

public class CitaNotFoundException extends RuntimeException {

    public CitaNotFoundException(String id) {
        super("No se encontró la cita con id: " + id);
    }
}