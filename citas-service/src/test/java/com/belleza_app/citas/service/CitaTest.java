package com.belleza_app.citas.service;

// ─────────────────────────────────────────────────────────────────────────────
//  PRUEBA UNITARIA — Entidad de dominio Cita
//
//  QUÉ prueba: las reglas de negocio puras de la entidad Cita.
//  CÓMO: Java puro, sin Spring, sin Mockito, sin base de datos.
//  POR QUÉ funciona sin Spring: Cita.java no tiene ningún import de Spring;
//    es una clase Java normal que podemos instanciar con "new".
// ─────────────────────────────────────────────────────────────────────────────

import com.belleza_app.citas.domain.entities.Cita;
import com.belleza_app.citas.domain.exceptions.CitaEstadoInvalidoException;
import com.belleza_app.citas.domain.exceptions.FechaCitaInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Cita — entidad de dominio (unit test)")
class CitaTest {

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Cita citaNueva() {
        return new Cita("Ana", "srv1", "prod1", LocalDateTime.now().plusDays(1));
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("estado inicial debe ser PENDIENTE")
        void estadoInicial_PENDIENTE() {
            // Arrange + Act
            Cita cita = citaNueva();
            // Assert
            assertThat(cita.getEstado()).isEqualTo(Cita.EstadoCita.PENDIENTE);
        }

        @Test
        @DisplayName("guarda correctamente todos los campos")
        void guardaCampos() {
            LocalDateTime fecha = LocalDateTime.now().plusDays(3);
            Cita cita = new Cita("Ana", "srv1", "prod1", fecha);

            assertThat(cita.getClienteNombre()).isEqualTo("Ana");
            assertThat(cita.getServicioId()).isEqualTo("srv1");
            assertThat(cita.getProductoId()).isEqualTo("prod1");
            assertThat(cita.getFechaHora()).isEqualTo(fecha);
        }

        @Test
        @DisplayName("lanza FechaCitaInvalidaException si la fecha es pasada")
        void lanza_siFechaPasada() {
            assertThatThrownBy(() ->
                    new Cita("Ana", "srv1", "prod1", LocalDateTime.now().minusDays(1)))
                    .isInstanceOf(FechaCitaInvalidaException.class);
        }

        @Test
        @DisplayName("lanza FechaCitaInvalidaException si la fecha es null")
        void lanza_siFechaNull() {
            assertThatThrownBy(() ->
                    new Cita("Ana", "srv1", "prod1", null))
                    .isInstanceOf(FechaCitaInvalidaException.class);
        }
    }

    // ── confirmar() ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("confirmar()")
    class Confirmar {

        @Test
        @DisplayName("PENDIENTE → CONFIRMADA")
        void pendiente_a_confirmada() {
            Cita cita = citaNueva();
            cita.confirmar();
            assertThat(cita.getEstado()).isEqualTo(Cita.EstadoCita.CONFIRMADA);
        }

        @Test
        @DisplayName("lanza CitaEstadoInvalidoException si ya está CANCELADA")
        void lanza_siCancelada() {
            Cita cita = citaNueva();
            cita.cancelar();

            assertThatThrownBy(cita::confirmar)
                    .isInstanceOf(CitaEstadoInvalidoException.class);
        }
    }

    // ── cancelar() ────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("cancelar()")
    class Cancelar {

        @Test
        @DisplayName("PENDIENTE → CANCELADA")
        void pendiente_a_cancelada() {
            Cita cita = citaNueva();
            cita.cancelar();
            assertThat(cita.getEstado()).isEqualTo(Cita.EstadoCita.CANCELADA);
        }

        @Test
        @DisplayName("CONFIRMADA → CANCELADA")
        void confirmada_a_cancelada() {
            Cita cita = citaNueva();
            cita.confirmar();
            cita.cancelar();
            assertThat(cita.getEstado()).isEqualTo(Cita.EstadoCita.CANCELADA);
        }

        @Test
        @DisplayName("lanza CitaEstadoInvalidoException si ya está CANCELADA")
        void lanza_siYaCancelada() {
            Cita cita = citaNueva();
            cita.cancelar();

            assertThatThrownBy(cita::cancelar)
                    .isInstanceOf(CitaEstadoInvalidoException.class);
        }
    }
}
