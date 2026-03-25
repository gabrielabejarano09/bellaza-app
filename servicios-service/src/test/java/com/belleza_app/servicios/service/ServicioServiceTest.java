package com.belleza_app.servicios.service;

import com.belleza_app.servicios.exception.ReglaNegocioServicioException;
import com.belleza_app.servicios.exception.ServicioNotFoundException;
import com.belleza_app.servicios.model.Servicio;
import com.belleza_app.servicios.repository.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTS para ServicioService.
 *
 * Qué son unit tests (lo que pide el profe):
 * - Prueban UNA sola unidad de código (el service) de forma AISLADA.
 * - Usan @Mock para reemplazar el repositorio con un objeto falso (Mockito).
 * - NO necesitan base de datos ni Spring levantado → son rapidísimos.
 * - Se prueban todos los caminos: feliz + errores.
 */
@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository repository; // objeto falso — no toca MongoDB

    @InjectMocks
    private ServicioService servicioService; // objeto REAL con el mock inyectado

    private Servicio servicioValido;

    @BeforeEach
    void setUp() {
        servicioValido = new Servicio("Corte de cabello", "Corte clásico", 25000, 60);
        servicioValido.setId("abc123");
    }

    // ─── crearServicio ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Crear servicio válido → debe guardarse y retornar ACTIVO")
    void crearServicio_valido_debeGuardar() {
        when(repository.save(any(Servicio.class))).thenReturn(servicioValido);

        Servicio resultado = servicioService.crearServicio(servicioValido);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(Servicio.EstadoServicio.ACTIVO);
        verify(repository, times(1)).save(any(Servicio.class));
    }

    @Test
    @DisplayName("Crear servicio con precio cero → debe lanzar ReglaNegocioServicioException")
    void crearServicio_precioCero_debeLanzarExcepcion() {
        servicioValido.setPrecio(0);

        assertThatThrownBy(() -> servicioService.crearServicio(servicioValido))
                .isInstanceOf(ReglaNegocioServicioException.class)
                .hasMessageContaining("mayor a cero");

        verify(repository, never()).save(any()); // no debe llamar al repo
    }

    @Test
    @DisplayName("Crear servicio con precio negativo → debe lanzar excepción")
    void crearServicio_precioNegativo_debeThrow() {
        servicioValido.setPrecio(-500);

        assertThatThrownBy(() -> servicioService.crearServicio(servicioValido))
                .isInstanceOf(ReglaNegocioServicioException.class);
    }

    @Test
    @DisplayName("Crear servicio con duración menor a 15 min → debe lanzar excepción")
    void crearServicio_duracionMenor15_debeThrow() {
        servicioValido.setDuracionMinutos(10);

        assertThatThrownBy(() -> servicioService.crearServicio(servicioValido))
                .isInstanceOf(ReglaNegocioServicioException.class)
                .hasMessageContaining("15 y 480");
    }

    @Test
    @DisplayName("Crear servicio con duración mayor a 480 min → debe lanzar excepción")
    void crearServicio_duracionMayor480_debeThrow() {
        servicioValido.setDuracionMinutos(500);

        assertThatThrownBy(() -> servicioService.crearServicio(servicioValido))
                .isInstanceOf(ReglaNegocioServicioException.class);
    }

    // ─── buscarPorId ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Buscar servicio existente → debe retornarlo")
    void buscarPorId_existe_debeRetornar() {
        when(repository.findById("abc123")).thenReturn(Optional.of(servicioValido));

        Servicio resultado = servicioService.buscarPorId("abc123");

        assertThat(resultado.getId()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("Buscar servicio inexistente → debe lanzar ServicioNotFoundException")
    void buscarPorId_noExiste_debeThrow() {
        when(repository.findById("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicioService.buscarPorId("noexiste"))
                .isInstanceOf(ServicioNotFoundException.class)
                .hasMessageContaining("noexiste");
    }

    // ─── esServicioActivo ────────────────────────────────────────────────────

    @Test
    @DisplayName("Servicio ACTIVO → esServicioActivo debe retornar true")
    void esServicioActivo_activo_debeRetornarTrue() {
        servicioValido.setEstado(Servicio.EstadoServicio.ACTIVO);
        when(repository.findById("abc123")).thenReturn(Optional.of(servicioValido));

        assertThat(servicioService.esServicioActivo("abc123")).isTrue();
    }

    @Test
    @DisplayName("Servicio INACTIVO → esServicioActivo debe retornar false")
    void esServicioActivo_inactivo_debeRetornarFalse() {
        servicioValido.setEstado(Servicio.EstadoServicio.INACTIVO);
        when(repository.findById("abc123")).thenReturn(Optional.of(servicioValido));

        assertThat(servicioService.esServicioActivo("abc123")).isFalse();
    }

    @Test
    @DisplayName("Servicio inexistente → esServicioActivo debe retornar false")
    void esServicioActivo_noExiste_debeRetornarFalse() {
        when(repository.findById("noexiste")).thenReturn(Optional.empty());

        assertThat(servicioService.esServicioActivo("noexiste")).isFalse();
    }

    // ─── activar / desactivar ────────────────────────────────────────────────

    @Test
    @DisplayName("Activar servicio → estado debe cambiar a ACTIVO")
    void activarServicio_debePonerActivo() {
        servicioValido.setEstado(Servicio.EstadoServicio.INACTIVO);
        when(repository.findById("abc123")).thenReturn(Optional.of(servicioValido));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Servicio resultado = servicioService.activarServicio("abc123");

        assertThat(resultado.getEstado()).isEqualTo(Servicio.EstadoServicio.ACTIVO);
    }

    @Test
    @DisplayName("Desactivar servicio → estado debe cambiar a INACTIVO")
    void desactivarServicio_debePoner_INACTIVO() {
        when(repository.findById("abc123")).thenReturn(Optional.of(servicioValido));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Servicio resultado = servicioService.desactivarServicio("abc123");

        assertThat(resultado.getEstado()).isEqualTo(Servicio.EstadoServicio.INACTIVO);
    }
}