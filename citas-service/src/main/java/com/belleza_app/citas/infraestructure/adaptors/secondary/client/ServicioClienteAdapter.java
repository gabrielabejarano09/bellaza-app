package com.belleza_app.citas.infraestructure.adaptors.secondary.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.belleza_app.citas.domain.ports.out.ServicioClientePuerto;

@Component
public class ServicioClienteAdapter implements ServicioClientePuerto {
    private final RestTemplate restTemplate;
    private final String serviciosUrl;

    public ServicioClienteAdapter(RestTemplate restTemplate,
            @Value("${servicios-service.url}") String serviciosUrl) {
        this.restTemplate = restTemplate;
        this.serviciosUrl = serviciosUrl;
    }

    @Override
    public boolean esServicioActivo(String servicioId) {
        String url = serviciosUrl + "/api/servicios/" + servicioId + "/activo";
        Boolean activo = restTemplate.getForObject(url, Boolean.class);
        return Boolean.TRUE.equals(activo);
    }
}
