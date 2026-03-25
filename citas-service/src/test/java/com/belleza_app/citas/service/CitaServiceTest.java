package com.belleza_app.citas.service;

import com.belleza_app.citas.dto.CitaRequest;
import com.belleza_app.citas.exception.CitaConflictoException;
import com.belleza_app.citas.model.Cita;
import com.belleza_app.citas.repository.CitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CitaService citaService;

    private CitaRequest request;

    @BeforeEach
    void setUp() {
        request = new CitaRequest();
        request.setClienteNombre("Isabela");
        request.setServicioId("serv1");
        request.setProductoId("prod1");
        request.setFechaHora(LocalDateTime.now().plusDays(1));

        ReflectionTestUtils.setField(citaService, "serviciosServiceUrl", "http://localhost:8082");
        ReflectionTestUtils.setField(citaService, "inventarioServiceUrl", "http://localhost:8083");
    }

    @Test
    @DisplayName("Crear cita válida")
    void crearCita_valida() {

        when(repository.findByClienteNombreAndFechaHora(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        when(repository.save(any(Cita.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Cita cita = citaService.crearCita(request);

        assertThat(cita.getEstado()).isEqualTo(Cita.EstadoCita.PENDIENTE);
        assertThat(cita.getClienteNombre()).isEqualTo("Isabela");
    }

    @Test
    @DisplayName("Crear cita con horario ocupado")
    void crearCita_horarioOcupado() {

        Cita yaExistente = new Cita();

        when(repository.findByClienteNombreAndFechaHora(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(yaExistente));

        assertThatThrownBy(() -> citaService.crearCita(request))
                .isInstanceOf(CitaConflictoException.class);
    }
}