package com.belleza_app.servicios.service;

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.exceptions.ReglaNegocioServicioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Servicio — entidad de dominio (unit test)")
class ServicioTest {

    private Servicio servicioValido() {
        return new Servicio("Corte", "Corte clásico", 25000, 60);
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("estado inicial debe ser ACTIVO")
        void estadoInicial_ACTIVO() {
            assertThat(servicioValido().getEstado())
                    .isEqualTo(Servicio.EstadoServicio.ACTIVO);
        }

        @Test
        @DisplayName("guarda correctamente todos los campos")
        void guardaCampos() {
            Servicio s = new Servicio("Manicure", "Manicure completo", 30000, 45);

            assertThat(s.getNombre()).isEqualTo("Manicure");
            assertThat(s.getDescripcion()).isEqualTo("Manicure completo");
            assertThat(s.getPrecio()).isEqualTo(30000);
            assertThat(s.getDuracionMinutos()).isEqualTo(45);
        }
    }

    // ── validarPrecio() ───────────────────────────────────────────────────────
    @Nested
    @DisplayName("validarPrecio()")
    class ValidarPrecio {

        @Test
        @DisplayName("precio positivo no lanza excepción")
        void precio_positivo_sinError() {
            assertThatCode(() -> servicioValido().validarPrecio(1000))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("precio cero lanza ReglaNegocioServicioException")
        void precio_cero_lanzaExcepcion() {
            assertThatThrownBy(() -> servicioValido().validarPrecio(0))
                    .isInstanceOf(ReglaNegocioServicioException.class)
                    .hasMessageContaining("mayor a cero");
        }

        @Test
        @DisplayName("precio negativo lanza ReglaNegocioServicioException")
        void precio_negativo_lanzaExcepcion() {
            assertThatThrownBy(() -> servicioValido().validarPrecio(-500))
                    .isInstanceOf(ReglaNegocioServicioException.class);
        }
    }

    // ── validarDuracion() ─────────────────────────────────────────────────────
    @Nested
    @DisplayName("validarDuracion()")
    class ValidarDuracion {

        @Test
        @DisplayName("duración en rango [15, 480] no lanza excepción")
        void duracion_enRango_sinError() {
            Servicio s = servicioValido();
            assertThatCode(() -> s.validarDuracion(15)).doesNotThrowAnyException();
            assertThatCode(() -> s.validarDuracion(240)).doesNotThrowAnyException();
            assertThatCode(() -> s.validarDuracion(480)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("duración menor a 15 lanza ReglaNegocioServicioException")
        void duracion_menor15_lanzaExcepcion() {
            assertThatThrownBy(() -> servicioValido().validarDuracion(14))
                    .isInstanceOf(ReglaNegocioServicioException.class)
                    .hasMessageContaining("15 y 480");
        }

        @Test
        @DisplayName("duración mayor a 480 lanza ReglaNegocioServicioException")
        void duracion_mayor480_lanzaExcepcion() {
            assertThatThrownBy(() -> servicioValido().validarDuracion(481))
                    .isInstanceOf(ReglaNegocioServicioException.class);
        }
    }
}