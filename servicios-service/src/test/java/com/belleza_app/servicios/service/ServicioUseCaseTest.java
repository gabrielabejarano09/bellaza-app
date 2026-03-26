package com.belleza_app.servicios.service;

// ─────────────────────────────────────────────────────────────────────────────
//  PRUEBAS UNITARIAS — Casos de uso del módulo servicios
//
//  QUÉ prueba: la lógica de cada use case de forma aislada.
//  CÓMO: @ExtendWith(MockitoExtension) — sin Spring.
//    ServicioRepositorioPuerto se mockea con @Mock (interfaz pura).
//    Cada use case se crea con "new" en @BeforeEach.
// ─────────────────────────────────────────────────────────────────────────────

import com.belleza_app.servicios.application.usecase.*;
import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.domain.exceptions.ReglaNegocioServicioException;
import com.belleza_app.servicios.domain.exceptions.ServicioNotFoundException;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Casos de uso — servicios (unit tests)")
class ServicioUseCaseTest {

    // Puerto mockeado — interfaz pura, sin Spring ni MongoDB
    @Mock ServicioRepositorioPuerto repo;

    private static final String ID = "srv-001";

    /** Crea un servicio de ejemplo válido y activo. */
    private Servicio servicioActivo() {
        Servicio s = new Servicio("Corte", "Corte clásico", 25000, 60);
        s.setId(ID);
        return s;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CrearServicioUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("CrearServicioUseCase")
    class CrearServicio {

        CrearServicioUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new CrearServicioUseCase(repo); }

        @Test
        @DisplayName("camino feliz: guarda servicio con estado ACTIVO")
        void exitoso_creaConEstadoActivo() {
            Servicio s = servicioActivo();
            given(repo.guardar(any())).willReturn(s);

            Servicio resultado = useCase.ejecutar(s);

            assertThat(resultado.getEstado()).isEqualTo(Servicio.EstadoServicio.ACTIVO);
            verify(repo).guardar(s);
        }

        @Test
        @DisplayName("lanza ReglaNegocioServicioException si precio es cero")
        void lanza_siPrecioCero() {
            Servicio s = servicioActivo();
            s.setPrecio(0);

            assertThatThrownBy(() -> useCase.ejecutar(s))
                    .isInstanceOf(ReglaNegocioServicioException.class)
                    .hasMessageContaining("mayor a cero");
        }

        @Test
        @DisplayName("lanza ReglaNegocioServicioException si precio es negativo")
        void lanza_siPrecioNegativo() {
            Servicio s = servicioActivo();
            s.setPrecio(-500);

            assertThatThrownBy(() -> useCase.ejecutar(s))
                    .isInstanceOf(ReglaNegocioServicioException.class);
        }

        @Test
        @DisplayName("lanza ReglaNegocioServicioException si duración es menor a 15 min")
        void lanza_siDuracionMenor15() {
            Servicio s = servicioActivo();
            s.setDuracionMinutos(10);

            assertThatThrownBy(() -> useCase.ejecutar(s))
                    .isInstanceOf(ReglaNegocioServicioException.class)
                    .hasMessageContaining("15 y 480");
        }

        @Test
        @DisplayName("lanza ReglaNegocioServicioException si duración es mayor a 480 min")
        void lanza_siDuracionMayor480() {
            Servicio s = servicioActivo();
            s.setDuracionMinutos(600);

            assertThatThrownBy(() -> useCase.ejecutar(s))
                    .isInstanceOf(ReglaNegocioServicioException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  BuscarServicioUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("BuscarServicioUseCase")
    class BuscarServicio {

        BuscarServicioUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new BuscarServicioUseCase(repo); }

        @Test
        @DisplayName("camino feliz: devuelve el servicio cuando existe")
        void exitoso_devuelveServicio() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(servicioActivo()));

            Servicio resultado = useCase.ejecutar(ID);

            assertThat(resultado.getId()).isEqualTo(ID);
        }

        @Test
        @DisplayName("lanza ServicioNotFoundException si el servicio no existe")
        void lanza_siNoExiste() {
            given(repo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(ServicioNotFoundException.class)
                    .hasMessageContaining(ID);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ActivarServicioUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ActivarServicioUseCase")
    class ActivarServicio {

        ActivarServicioUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ActivarServicioUseCase(repo); }

        @Test
        @DisplayName("camino feliz: activa un servicio inactivo")
        void exitoso_activaServicio() {
            Servicio s = servicioActivo();
            s.setEstado(Servicio.EstadoServicio.INACTIVO);
            given(repo.buscarPorId(ID)).willReturn(Optional.of(s));
            given(repo.guardar(any())).willAnswer(inv -> inv.getArgument(0));

            Servicio resultado = useCase.ejecutar(ID);

            assertThat(resultado.getEstado()).isEqualTo(Servicio.EstadoServicio.ACTIVO);
        }

        @Test
        @DisplayName("lanza ServicioNotFoundException si el servicio no existe")
        void lanza_siNoExiste() {
            given(repo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(ServicioNotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DesactivarServicioUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("DesactivarServicioUseCase")
    class DesactivarServicio {

        DesactivarServicioUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new DesactivarServicioUseCase(repo); }

        @Test
        @DisplayName("camino feliz: desactiva un servicio activo")
        void exitoso_desactivaServicio() {
            given(repo.buscarPorId(ID)).willReturn(Optional.of(servicioActivo()));
            given(repo.guardar(any())).willAnswer(inv -> inv.getArgument(0));

            Servicio resultado = useCase.ejecutar(ID);

            assertThat(resultado.getEstado()).isEqualTo(Servicio.EstadoServicio.INACTIVO);
        }

        @Test
        @DisplayName("lanza ServicioNotFoundException si el servicio no existe")
        void lanza_siNoExiste() {
            given(repo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(ServicioNotFoundException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  EsServicioActivoUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("EsServicioActivoUseCase")
    class EsServicioActivo {

        EsServicioActivoUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new EsServicioActivoUseCase(repo); }

        @Test
        @DisplayName("devuelve true cuando el servicio está activo")
        void true_cuandoActivo() {
            given(repo.existeActivo(ID)).willReturn(true);
            assertThat(useCase.ejecutar(ID)).isTrue();
        }

        @Test
        @DisplayName("devuelve false cuando el servicio está inactivo o no existe")
        void false_cuandoInactivoONoExiste() {
            given(repo.existeActivo(ID)).willReturn(false);
            assertThat(useCase.ejecutar(ID)).isFalse();
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ListarServiciosActivosUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ListarServiciosActivosUseCase")
    class ListarActivos {

        ListarServiciosActivosUseCase useCase;

        @BeforeEach
        void setUp() { useCase = new ListarServiciosActivosUseCase(repo); }

        @Test
        @DisplayName("devuelve todos los servicios activos del repositorio")
        void devuelveListaActivos() {
            List<Servicio> activos = List.of(servicioActivo(), servicioActivo());
            given(repo.buscarActivos()).willReturn(activos);

            List<Servicio> resultado = useCase.ejecutar();

            assertThat(resultado).hasSize(2);
        }

        @Test
        @DisplayName("devuelve lista vacía cuando no hay servicios activos")
        void devuelveListaVacia_sinActivos() {
            given(repo.buscarActivos()).willReturn(List.of());

            assertThat(useCase.ejecutar()).isEmpty();
        }
    }
}