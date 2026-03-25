package com.belleza_app.citas.exception;

public class ServicioInactivoException extends RuntimeException {

    public ServicioInactivoException(String servicioId) {
        super("No se puede confirmar la cita porque el servicio con id " + servicioId + " está INACTIVO");
    }
}