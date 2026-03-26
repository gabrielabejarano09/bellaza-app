package com.belleza_app.inventario.infraestructure.adaptors.primary.rest;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.belleza_app.inventario.domain.entities.Producto;
import com.belleza_app.inventario.domain.ports.in.AgregarStockEntrada;
import com.belleza_app.inventario.domain.ports.in.ConsultarStockEntrada;
import com.belleza_app.inventario.domain.ports.in.DescontarStockEntrada;
import com.belleza_app.inventario.domain.ports.in.HayStockDisponibleEntrada;
import com.belleza_app.inventario.domain.ports.in.RegistrarProductoEntrada;
import com.belleza_app.inventario.infraestructure.adaptors.primary.rest.dto.StockRequest;
import com.belleza_app.inventario.infraestructure.adaptors.primary.rest.dto.StockResponse;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final RegistrarProductoEntrada registrarProducto;
    private final ConsultarStockEntrada consultarStock;
    private final AgregarStockEntrada agregarStock;
    private final DescontarStockEntrada descontarStock;
    private final HayStockDisponibleEntrada hayStockDisponible;

    public ProductoController(
            RegistrarProductoEntrada registrarProducto,
            ConsultarStockEntrada consultarStock,
            AgregarStockEntrada agregarStock,
            DescontarStockEntrada descontarStock,
            HayStockDisponibleEntrada hayStockDisponible
    ) {
        this.registrarProducto = registrarProducto;
        this.consultarStock = consultarStock;
        this.agregarStock = agregarStock;
        this.descontarStock = descontarStock;
        this.hayStockDisponible = hayStockDisponible;
    }

    @PostMapping
    public ResponseEntity<Producto> registrarProducto(@Valid @RequestBody Producto producto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrarProducto.ejecutar(producto));
    }

    @GetMapping("/{id}/stock")
public ResponseEntity<StockResponse> consultarStock(@PathVariable String id) {

    Producto producto = consultarStock.ejecutar(id);

    StockResponse response = new StockResponse(
        producto.getId(),
        producto.getNombre(),
        producto.getStock(),
        producto.getStock() > 0
    );

    return ResponseEntity.ok(response);
}

    @PatchMapping("/{id}/agregar-stock")
    public ResponseEntity<Producto> agregarStock(@PathVariable String id,
                                                 @Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(agregarStock.ejecutar(id, request.getCantidad()));
    }

    @PatchMapping("/{id}/descontar-stock")
    public ResponseEntity<Producto> descontarStock(@PathVariable String id,
                                                   @Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(descontarStock.ejecutar(id, request.getCantidad()));
    }

    @GetMapping("/{id}/disponible")
    public ResponseEntity<Boolean> disponible(@PathVariable String id,
                                              @RequestParam int cantidad) {
        return ResponseEntity.ok(hayStockDisponible.ejecutar(id, cantidad));
    }
}