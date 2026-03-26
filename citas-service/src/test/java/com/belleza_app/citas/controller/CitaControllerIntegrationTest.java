package com.belleza_app.citas.controller;

import com.belleza_app.citas.domain.ports.out.InventarioClientePuerto;
import com.belleza_app.citas.domain.ports.out.ServicioClientePuerto;
import com.belleza_app.citas.infraestructure.adaptors.primary.rest.dto.CitaRequest;
import com.belleza_app.citas.infraestructure.adaptors.secondary.persistence.CitaMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")         // carga src/test/resources/application-test.yaml
@DisplayName("CitaController — integración (HTTP → UseCase → MongoDB)")
class CitaControllerIntegrationTest {

    @Autowired MockMvc             mockMvc;
    @Autowired ObjectMapper        objectMapper;
    @Autowired CitaMongoRepository mongoRepo;   // MongoDB real (Flapdoodle)

    // Solo mockeamos puertos de servicios externos — MongoDB es REAL
    @MockBean ServicioClientePuerto   servicioCliente;
    @MockBean InventarioClientePuerto inventarioCliente;

    @BeforeEach
    void setUp() {
        mongoRepo.deleteAll();
        // Por defecto todo activo y con stock para no repetir esto en cada test
        given(servicioCliente.esServicioActivo(any())).willReturn(true);
        given(inventarioCliente.hayStockDisponible(any(), anyInt())).willReturn(true);
    }

    // ── POST /api/citas ───────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/citas → 201 y registra en MongoDB")
    void post_retorna201_yPersiste() throws Exception {
        CitaRequest req = request("Gabriela", "serv1", "prod1",
                LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteNombre", is("Gabriela")))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")))
                .andExpect(jsonPath("$.id", notNullValue()));

        assertThat(mongoRepo.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/citas → 422 cuando el servicio está inactivo")
    void post_retorna422_siServicioInactivo() throws Exception {
        given(servicioCliente.esServicioActivo(any())).willReturn(false);

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request("Ana", "serv1", "prod1",
                                LocalDateTime.now().plusDays(1)))))
                .andExpect(status().isUnprocessableEntity());

        assertThat(mongoRepo.findAll()).isEmpty();
    }

    @Test
    @DisplayName("POST /api/citas → 400 cuando faltan campos obligatorios")
    void post_retorna400_siFaltanCampos() throws Exception {
        // Cuerpo vacío — violará @NotBlank y @Future
        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/citas → 422 cuando hay conflicto de horario (dos citas iguales)")
    void post_retorna422_siConflictoHorario() throws Exception {
        LocalDateTime fecha = LocalDateTime.now().plusDays(1);
        CitaRequest req = request("Gabriela", "serv1", "prod1", fecha);

        // Primera → OK
        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isCreated());

        // Segunda con mismo cliente y horario → conflicto
        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── PATCH /{id}/confirmar ─────────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /{id}/confirmar → 200 y estado CONFIRMADA")
    void confirmar_retorna200_estadoConfirmada() throws Exception {
        String id = crearCita("Gabriela", "serv1", "prod1");

        mockMvc.perform(patch("/api/citas/" + id + "/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CONFIRMADA")));
    }

    @Test
    @DisplayName("PATCH /{id}/confirmar → 422 cuando no hay stock")
    void confirmar_retorna422_sinStock() throws Exception {
        String id = crearCita("Gabriela", "serv1", "prod1");
        given(inventarioCliente.hayStockDisponible(any(), anyInt())).willReturn(false);

        mockMvc.perform(patch("/api/citas/" + id + "/confirmar"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH /{id}/confirmar → 404 si la cita no existe")
    void confirmar_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(patch("/api/citas/id-fantasma/confirmar"))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /{id}/cancelar ──────────────────────────────────────────────────

    @Test
    @DisplayName("PATCH /{id}/cancelar → 200 y estado CANCELADA")
    void cancelar_retorna200_estadoCancelada() throws Exception {
        String id = crearCita("Gabriela", "serv1", "prod1");

        mockMvc.perform(patch("/api/citas/" + id + "/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CANCELADA")));
    }

    @Test
    @DisplayName("PATCH /{id}/cancelar → 404 si la cita no existe")
    void cancelar_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(patch("/api/citas/id-fantasma/cancelar"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /{id}/cancelar → 422 al cancelar una cita ya cancelada")
    void cancelar_retorna422_siYaCancelada() throws Exception {
        String id = crearCita("Gabriela", "serv1", "prod1");
        mockMvc.perform(patch("/api/citas/" + id + "/cancelar")).andExpect(status().isOk());

        // Segundo intento de cancelar la misma cita
        mockMvc.perform(patch("/api/citas/" + id + "/cancelar"))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── GET /{id} ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /{id} → 200 con datos correctos")
    void buscar_retorna200_conDatos() throws Exception {
        String id = crearCita("Gabriela", "serv1", "prod1");

        mockMvc.perform(get("/api/citas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.clienteNombre", is("Gabriela")));
    }

    @Test
    @DisplayName("GET /{id} → 404 si la cita no existe")
    void buscar_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(get("/api/citas/id-fantasma"))
                .andExpect(status().isNotFound());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private CitaRequest request(String cliente, String servId, String prodId,
                                LocalDateTime fecha) {
        CitaRequest r = new CitaRequest();
        r.setClienteNombre(cliente);
        r.setServicioId(servId);
        r.setProductoId(prodId);
        r.setFechaHora(fecha);
        return r;
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /** Crea una cita vía HTTP y devuelve el id asignado por MongoDB. */
    private String crearCita(String cliente, String servId, String prodId) throws Exception {
        String resp = mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request(cliente, servId, prodId,
                                LocalDateTime.now().plusDays(1)))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("id").asText();
    }
}