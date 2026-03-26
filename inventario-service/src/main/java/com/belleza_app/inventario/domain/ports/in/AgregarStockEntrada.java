package com.belleza_app.inventario.domain.ports.in;

import com.belleza_app.inventario.domain.entities.Producto;

public interface AgregarStockEntrada   { Producto ejecutar(String id, int cantidad); }
