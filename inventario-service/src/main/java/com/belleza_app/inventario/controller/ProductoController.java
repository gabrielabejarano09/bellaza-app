package com.belleza_app.inventario.controller;

import com.belleza_app.inventario.dto.StockRequest;
import com.belleza_app.inventario.dto.StockResponse;
import com.belleza_app.inventario.model.Producto;
import com.belleza_app.inventario.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<Producto> registrarProducto(@Valid @RequestBody Producto producto) {
        Producto creado = productoService.registrarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<StockResponse> consultarStock(@PathVariable String id) {
        return ResponseEntity.ok(productoService.consultarStock(id));
    }

    @PatchMapping("/{id}/agregar-stock")
    public ResponseEntity<Producto> agregarStock(@PathVariable String id,
                                                 @Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(productoService.agregarStock(id, request.getCantidad()));
    }

    @PatchMapping("/{id}/descontar-stock")
    public ResponseEntity<Producto> descontarStock(@PathVariable String id,
                                                   @Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(productoService.descontarStock(id, request.getCantidad()));
    }

    @GetMapping("/{id}/disponible")
    public ResponseEntity<Boolean> disponible(@PathVariable String id,
                                              @RequestParam int cantidad) {
        return ResponseEntity.ok(productoService.hayStockDisponible(id, cantidad));
    }
}