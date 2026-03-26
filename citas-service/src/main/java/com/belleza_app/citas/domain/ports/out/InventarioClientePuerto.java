package com.belleza_app.citas.domain.ports.out;

public interface InventarioClientePuerto {
    boolean hayStockDisponible(String productoId, int cantidad);
}