package com.belleza_app.servicios.domain.ports.in;

import com.belleza_app.servicios.domain.entities.Servicio;

public interface ActivarServicioEntrada  { 
    Servicio ejecutar(String id); 
}