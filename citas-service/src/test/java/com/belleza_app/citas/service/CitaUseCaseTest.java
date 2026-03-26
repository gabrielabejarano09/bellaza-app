package com.belleza_app.citas.service;

import com.belleza_app.citas.application.usecase.*;
import com.belleza_app.citas.domain.entities.Cita;
import com.belleza_app.citas.domain.exceptions.*;
import com.belleza_app.citas.domain.ports.out.CitaRepositorioPuerto;
import com.belleza_app.citas.domain.ports.out.InventarioClientePuerto;
import com.belleza_app.citas.domain.ports.out.ServicioClientePuerto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Casos de uso — citas (unit tests)")
class CitaUseCaseTest {

    // ── Puertos mockeados (interfaces puras, SIN Spring) ──────────────────────
    @Mock CitaRepositorioPuerto   citaRepo;
    @Mock ServicioClientePuerto   servicioCliente;
    @Mock InventarioClientePuerto inventarioCliente;

    // ── Datos reutilizables ───────────────────────────────────────────────────
    private static final String        ID            = "cita-001";
    private static final String        CLIENTE       = "Gabriela";
    private static final String        SERVICIO_ID   = "serv1";
    private static final String        PRODUCTO_ID   = "prod1";
    private static final LocalDateTime FECHA_FUTURA  = LocalDateTime.now().plusDays(2);

    /** Crea una Cita de dominio lista para reutilizar en los tests. */
    private Cita citaPendiente() {
        Cita c = new Cita(CLIENTE, SERVICIO_ID, PRODUCTO_ID, FECHA_FUTURA);
        c.setId(ID);
        return c;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  AgendarCitaUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("AgendarCitaUseCase")
    class AgendarCita {

        // Use case creado con constructor directo — sin Spring, sin @Autowired
        AgendarCitaUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new AgendarCitaUseCase(citaRepo, servicioCliente);
        }

        @Test
        @DisplayName("camino feliz: devuelve cita PENDIENTE cuando servicio activo y sin conflicto")
        void exitoso_devuelvePendiente() {
            // Arrange — configuramos qué devuelven los puertos
            given(servicioCliente.esServicioActivo(SERVICIO_ID)).willReturn(true);
            given(citaRepo.existeConflictoHorario(CLIENTE, FECHA_FUTURA)).willReturn(false);
            given(citaRepo.guardar(any())).willAnswer(inv -> inv.getArgument(0));

            // Act
            Cita resultado = useCase.ejecutar(CLIENTE, SERVICIO_ID, PRODUCTO_ID, FECHA_FUTURA);

            // Assert
            assertThat(resultado.getEstado()).isEqualTo(Cita.EstadoCita.PENDIENTE);
            assertThat(resultado.getClienteNombre()).isEqualTo(CLIENTE);
            // Verifica que SÍ se llamó a guardar
            verify(citaRepo).guardar(any());
        }

        @Test
        @DisplayName("lanza ServicioInactivoException si el servicio está inactivo")
        void lanza_siServicioInactivo() {
            given(servicioCliente.esServicioActivo(SERVICIO_ID)).willReturn(false);

            assertThatThrownBy(() ->
                    useCase.ejecutar(CLIENTE, SERVICIO_ID, PRODUCTO_ID, FECHA_FUTURA))
                    .isInstanceOf(ServicioInactivoException.class);

            // El repositorio nunca debe tocarse si el servicio está inactivo
            verifyNoInteractions(citaRepo);
        }

        @Test
        @DisplayName("lanza CitaConflictoException si ya existe cita en ese horario")
        void lanza_siConflictoHorario() {
            given(servicioCliente.esServicioActivo(any())).willReturn(true);
            given(citaRepo.existeConflictoHorario(any(), any())).willReturn(true);

            assertThatThrownBy(() ->
                    useCase.ejecutar(CLIENTE, SERVICIO_ID, PRODUCTO_ID, FECHA_FUTURA))
                    .isInstanceOf(CitaConflictoException.class);
        }

        @Test
        @DisplayName("verifica el servicio ANTES de consultar conflictos de horario")
        void ordenDeValidacion_servicioAnteQueConflicto() {
            given(servicioCliente.esServicioActivo(any())).willReturn(false);

            assertThatThrownBy(() ->
                    useCase.ejecutar(CLIENTE, SERVICIO_ID, PRODUCTO_ID, FECHA_FUTURA))
                    .isInstanceOf(ServicioInactivoException.class);

            // Si falló en el primer paso, el repositorio no debe haberse consultado
            verifyNoInteractions(citaRepo);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ConfirmarCitaUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ConfirmarCitaUseCase")
    class ConfirmarCita {

        ConfirmarCitaUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ConfirmarCitaUseCase(citaRepo, inventarioCliente);
        }

        @Test
        @DisplayName("camino feliz: confirma la cita cuando hay stock")
        void exitoso_confirmaConStock() {
            given(citaRepo.buscarPorId(ID)).willReturn(Optional.of(citaPendiente()));
            given(inventarioCliente.hayStockDisponible(PRODUCTO_ID, 1)).willReturn(true);
            given(citaRepo.guardar(any())).willAnswer(inv -> inv.getArgument(0));

            Cita resultado = useCase.ejecutar(ID);

            assertThat(resultado.getEstado()).isEqualTo(Cita.EstadoCita.CONFIRMADA);
            verify(citaRepo).guardar(any());
        }

        @Test
        @DisplayName("lanza CitaNotFoundException si la cita no existe")
        void lanza_siCitaNoExiste() {
            given(citaRepo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(CitaNotFoundException.class);

            // Si no hay cita, no se consulta inventario ni se guarda nada
            verifyNoInteractions(inventarioCliente);
        }

        @Test
        @DisplayName("lanza ReglaNegocioCitaException cuando no hay stock suficiente")
        void lanza_siSinStock() {
            given(citaRepo.buscarPorId(ID)).willReturn(Optional.of(citaPendiente()));
            given(inventarioCliente.hayStockDisponible(any(), anyInt())).willReturn(false);

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(ReglaNegocioCitaException.class)
                    .hasMessageContaining("stock");
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CancelarCitaUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("CancelarCitaUseCase")
    class CancelarCita {

        CancelarCitaUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new CancelarCitaUseCase(citaRepo);
        }

        @Test
        @DisplayName("camino feliz: cancela y guarda la cita")
        void exitoso_cancelaYGuarda() {
            given(citaRepo.buscarPorId(ID)).willReturn(Optional.of(citaPendiente()));
            given(citaRepo.guardar(any())).willAnswer(inv -> inv.getArgument(0));

            Cita resultado = useCase.ejecutar(ID);

            assertThat(resultado.getEstado()).isEqualTo(Cita.EstadoCita.CANCELADA);
            verify(citaRepo).guardar(any());
        }

        @Test
        @DisplayName("lanza CitaNotFoundException si la cita no existe")
        void lanza_siCitaNoExiste() {
            given(citaRepo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(CitaNotFoundException.class);
        }

        @Test
        @DisplayName("lanza CitaEstadoInvalidoException al cancelar una cita ya cancelada")
        void lanza_siYaEstaCancelada() {
            Cita yaCanc = citaPendiente();
            yaCanc.cancelar(); // pre-cancelada en el mock
            given(citaRepo.buscarPorId(ID)).willReturn(Optional.of(yaCanc));

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(CitaEstadoInvalidoException.class);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ConsultarCitaUseCase
    // ═════════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("ConsultarCitaUseCase")
    class ConsultarCita {

        ConsultarCitaUseCase useCase;

        @BeforeEach
        void setUp() {
            useCase = new ConsultarCitaUseCase(citaRepo);
        }

        @Test
        @DisplayName("camino feliz: devuelve la cita cuando existe")
        void exitoso_devuelveCita() {
            Cita cita = citaPendiente();
            given(citaRepo.buscarPorId(ID)).willReturn(Optional.of(cita));

            Cita resultado = useCase.ejecutar(ID);

            assertThat(resultado.getId()).isEqualTo(ID);
            assertThat(resultado.getClienteNombre()).isEqualTo(CLIENTE);
        }

        @Test
        @DisplayName("lanza CitaNotFoundException si la cita no existe")
        void lanza_siNoExiste() {
            given(citaRepo.buscarPorId(ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> useCase.ejecutar(ID))
                    .isInstanceOf(CitaNotFoundException.class);
        }
    }
}