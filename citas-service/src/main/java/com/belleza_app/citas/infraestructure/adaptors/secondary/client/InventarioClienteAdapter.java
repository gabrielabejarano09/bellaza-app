package com.belleza_app.citas.infraestructure.adaptors.secondary.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.belleza_app.citas.domain.ports.out.InventarioClientePuerto;

@Component
public class InventarioClienteAdapter implements InventarioClientePuerto {
    private final RestTemplate restTemplate;
    private final String inventarioUrl;

    public InventarioClienteAdapter(RestTemplate restTemplate,
            @Value("${inventario-service.url}") String inventarioUrl) {
        this.restTemplate  = restTemplate;
        this.inventarioUrl = inventarioUrl;
    }

    @Override
    public boolean hayStockDisponible(String productoId, int cantidad) {
        String url = inventarioUrl + "/api/productos/" + productoId
                   + "/disponible?cantidad=" + cantidad;
        Boolean d = restTemplate.getForObject(url, Boolean.class);
        return Boolean.TRUE.equals(d);
    }
}
