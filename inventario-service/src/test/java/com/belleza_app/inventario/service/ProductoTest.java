package com.belleza_app.inventario.service;

// ─────────────────────────────────────────────────────────────────────────────
//  PRUEBA UNITARIA — Entidad de dominio Producto
//
//  QUÉ prueba: el comportamiento de la entidad Producto.
//  CÓMO: Java puro, sin Spring, sin mocks.
//    Producto no tiene reglas de negocio con lanzamiento de excepciones propias
//    (eso lo hace el use case), por lo que estos tests verifican
//    la construcción correcta del objeto y sus getters/setters.
// ─────────────────────────────────────────────────────────────────────────────

import com.belleza_app.inventario.domain.entities.Producto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Producto — entidad de dominio (unit test)")
class ProductoTest {

    @Test
    @DisplayName("constructor guarda nombre, descripción y stock correctamente")
    void constructor_guardaCampos() {
        Producto p = new Producto("Shampoo", "Shampoo profesional", 10);

        assertThat(p.getNombre()).isEqualTo("Shampoo");
        assertThat(p.getDescripcion()).isEqualTo("Shampoo profesional");
        assertThat(p.getStock()).isEqualTo(10);
    }

    @Test
    @DisplayName("stock puede actualizarse con setStock")
    void setStock_actualizaValor() {
        Producto p = new Producto("Cera", "Cera capilar", 5);
        p.setStock(20);
        assertThat(p.getStock()).isEqualTo(20);
    }

    @Test
    @DisplayName("id puede asignarse y recuperarse")
    void setId_asignaYRecupera() {
        Producto p = new Producto();
        p.setId("abc123");
        assertThat(p.getId()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("constructor sin argumentos crea objeto vacío sin errores")
    void constructorVacio_sinErrores() {
        Producto p = new Producto();
        assertThat(p).isNotNull();
        assertThat(p.getStock()).isZero();
    }
}