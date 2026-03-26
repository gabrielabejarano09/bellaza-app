package com.belleza_app.inventario.service;
// ─────────────────────────────────────────────────────────────────────────────
//  PRUEBAS UNITARIAS — Casos de uso del módulo inventario
//
//  QUÉ prueba: la lógica de cada use case de forma aislada.
//  CÓMO: @ExtendWith(MockitoExtension) — sin Spring.
//    ProductoRepositorioPuerto se mockea con @Mock.
//    Cada use case se construye con "new" en @BeforeEach.
// ─────────────────────────────────────────────────────────────────────────────

import com.belleza_app.inventario.application.usecase.*;
import com.belleza_app.inventario.domain.entities.Producto;
import com.belleza_app.inventario.domain.exceptions.ProductoNotFoundException;
import com.belleza_app.inventario.domain.exceptions.StockInsuficienteException;
import com.belleza_app.inventario.domain.ports.out.ProductoRepositorioPuerto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Casos de uso — inventario (unit tests)")
class InventarioUseCaseTest {

    // Puerto mockeado — interfaz pura, sin Spring ni MongoDB
    @Mock ProductoRepositorioPuerto repo;

    private static final String ID = "prod-001";

    /** Crea un producto de ejemplo con 10 unidades de stock. */
    private Producto producto(int stock) {
        Producto p = new Producto("Shampoo", "Profesional", stock);
        p.setId(ID);
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  RegistrarProductoUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("RegistrarProductoUseCase")
    class RegistrarProducto {

        RegistrarProductoUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new RegistrarProductoUseCase(repo); }

        @Test
        @DisplayName("camino feliz: guarda y devuelve el producto")
        void exitoso_guardaProducto() {
            Producto p = producto(10);
            given(repo.guardar(any())).willReturn(p);

            Producto resultado = useCase.ejecutar(p);

            assertThat(resultado.getNombre()).isEqualTo("Shampoo");
            assertThat(resultado.getStock()).isEqualTo(10);
            verify(repo).guardar(p);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ConsultarStockUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ConsultarStockUseCase")
    class ConsultarStock {

        ConsultarStockUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ConsultarStockUseCase(repo); }

        @Test
        @DisplayName("camino feliz: devuelve el producto cuando existe")
        void exitoso_devuelveProducto() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(producto(10)));

            Producto resultado = useCase.ejecutar(ID);

            assertThat(resultado.getId()).isEqualTo(ID);
            assertThat(resultado.getStock()).isEqualTo(10);
        }

        @Test
        @DisplayName("lanza ProductoNotFoundException si el producto no existe")
        void lanza_siNoExiste() {
            given(repo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(ProductoNotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  AgregarStockUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("AgregarStockUseCase")
    class AgregarStock {

        AgregarStockUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new AgregarStockUseCase(repo); }

        @Test
        @DisplayName("camino feliz: suma la cantidad al stock existente")
        void exitoso_sumaStock() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(producto(10)));
            given(repo.guardar(any())).willAnswer(inv -> inv.getArgument(0));

            Producto resultado = useCase.ejecutar(ID, 5);

            assertThat(resultado.getStock()).isEqualTo(15);
        }

        @Test
        @DisplayName("lanza ProductoNotFoundException si el producto no existe")
        void lanza_siProductoNoExiste() {
            given(repo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID, 5))
                    .isInstanceOf(ProductoNotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DescontarStockUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("DescontarStockUseCase")
    class DescontarStock {

        DescontarStockUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new DescontarStockUseCase(repo); }

        @Test
        @DisplayName("camino feliz: resta la cantidad del stock")
        void exitoso_restaStock() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(producto(10)));
            given(repo.guardar(any())).willAnswer(inv -> inv.getArgument(0));

            Producto resultado = useCase.ejecutar(ID, 3);

            assertThat(resultado.getStock()).isEqualTo(7);
        }

        @Test
        @DisplayName("lanza StockInsuficienteException si la cantidad es 0 o negativa")
        void lanza_siCantidadCero() {
            assertThatThrownBy(() -> useCase.ejecutar(ID, 0))
                    .isInstanceOf(StockInsuficienteException.class);
        }

        @Test
        @DisplayName("lanza StockInsuficienteException si se pide más de lo disponible")
        void lanza_siStockInsuficiente() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(producto(5)));

            assertThatThrownBy(() -> useCase.ejecutar(ID, 10))
                    .isInstanceOf(StockInsuficienteException.class);
        }

        @Test
        @DisplayName("lanza ProductoNotFoundException si el producto no existe")
        void lanza_siProductoNoExiste() {
            given(repo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID, 3))
                    .isInstanceOf(ProductoNotFoundException.class);
        }

        @Test
        @DisplayName("descontar exactamente el stock disponible → stock queda en 0")
        void descontarTodo_stockEnCero() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(producto(5)));
            given(repo.guardar(any())).willAnswer(inv -> inv.getArgument(0));

            Producto resultado = useCase.ejecutar(ID, 5);

            assertThat(resultado.getStock()).isZero();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HayStockDisponibleUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("HayStockDisponibleUseCase")
    class HayStockDisponible {

        HayStockDisponibleUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new HayStockDisponibleUseCase(repo); }

        @Test
        @DisplayName("devuelve true cuando el stock es mayor o igual a la cantidad pedida")
        void true_cuandoHayStock() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(producto(10)));

            assertThat(useCase.ejecutar(ID, 5)).isTrue();
            assertThat(useCase.ejecutar(ID, 10)).isTrue();
        }

        @Test
        @DisplayName("devuelve false cuando el stock es insuficiente")
        void false_cuandoStockInsuficiente() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(producto(3)));

            assertThat(useCase.ejecutar(ID, 5)).isFalse();
        }

        @Test
        @DisplayName("devuelve false cuando el producto no existe")
        void false_cuandoProductoNoExiste() {
            given(repo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThat(useCase.ejecutar(ID, 1)).isFalse();
        }
    }
}