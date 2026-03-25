package com.belleza_app.inventario.service;

import com.belleza_app.inventario.dto.StockResponse;
import com.belleza_app.inventario.exception.ProductoNotFoundException;
import com.belleza_app.inventario.exception.StockInsuficienteException;
import com.belleza_app.inventario.model.Producto;
import com.belleza_app.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService productoService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId("prod1");
        producto.setNombre("Shampoo");
        producto.setDescripcion("Shampoo profesional");
        producto.setStock(10);
    }

    @Test
    @DisplayName("Registrar producto válido")
    void registrarProducto_valido() {
        when(repository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.registrarProducto(producto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Shampoo");
        assertThat(resultado.getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("Registrar producto con stock negativo")
    void registrarProducto_stockNegativo() {
        Producto nuevo = new Producto();
        nuevo.setNombre("Acondicionador");
        nuevo.setDescripcion("Acondicionador premium");
        nuevo.setStock(-1);

        assertThatThrownBy(() -> productoService.registrarProducto(nuevo))
                .isInstanceOf(StockInsuficienteException.class);
    }

    @Test
    @DisplayName("Buscar producto existente")
    void buscarPorId_existente() {
        when(repository.findById("prod1")).thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarPorId("prod1");

        assertThat(resultado.getId()).isEqualTo("prod1");
        assertThat(resultado.getNombre()).isEqualTo("Shampoo");
    }

    @Test
    @DisplayName("Buscar producto inexistente")
    void buscarPorId_inexistente() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.buscarPorId("x"))
                .isInstanceOf(ProductoNotFoundException.class);
    }

    @Test
    @DisplayName("Agregar stock correctamente")
    void agregarStock_correctamente() {
        when(repository.findById("prod1")).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Producto.class));

        Producto resultado = productoService.agregarStock("prod1", 5);

        assertThat(resultado.getStock()).isEqualTo(15);
    }

    @Test
    @DisplayName("Agregar stock con cantidad inválida")
    void agregarStock_cantidadInvalida() {
        assertThatThrownBy(() -> productoService.agregarStock("prod1", 0))
                .isInstanceOf(StockInsuficienteException.class);
    }

    @Test
    @DisplayName("Descontar stock suficiente")
    void descontarStock_suficiente() {
        when(repository.findById("prod1")).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Producto.class));

        Producto resultado = productoService.descontarStock("prod1", 3);

        assertThat(resultado.getStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("Descontar stock insuficiente")
    void descontarStock_insuficiente() {
        when(repository.findById("prod1")).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> productoService.descontarStock("prod1", 20))
                .isInstanceOf(StockInsuficienteException.class);
    }

    @Test
    @DisplayName("Consultar stock")
    void consultarStock() {
        when(repository.findById("prod1")).thenReturn(Optional.of(producto));

        StockResponse response = productoService.consultarStock("prod1");

        assertThat(response).isNotNull();
        assertThat(response.getProductoId()).isEqualTo("prod1");
        assertThat(response.getStockActual()).isEqualTo(10);
        assertThat(response.isDisponible()).isTrue();
    }

    @Test
    @DisplayName("Validar que hay stock disponible")
    void hayStockDisponible_true() {
        when(repository.findById("prod1")).thenReturn(Optional.of(producto));

        boolean disponible = productoService.hayStockDisponible("prod1", 5);

        assertThat(disponible).isTrue();
    }

    @Test
    @DisplayName("Validar que no hay stock disponible")
    void hayStockDisponible_false() {
        when(repository.findById("prod1")).thenReturn(Optional.of(producto));

        boolean disponible = productoService.hayStockDisponible("prod1", 50);

        assertThat(disponible).isFalse();
    }
}