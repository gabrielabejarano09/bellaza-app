package com.belleza_app.inventario.dto;

import jakarta.validation.constraints.Min;

public class StockRequest {

    @Min(value = 1, message = "La cantidad debe ser mayor a cero")
    private int cantidad;

    public StockRequest() {
    }

    public StockRequest(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}