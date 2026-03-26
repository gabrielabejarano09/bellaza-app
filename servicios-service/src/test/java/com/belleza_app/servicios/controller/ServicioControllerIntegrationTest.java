package com.belleza_app.servicios.controller;

// ─────────────────────────────────────────────────────────────────────────────
//  PRUEBA DE INTEGRACIÓN — ServicioController
//
//  QUÉ prueba: el flujo completo HTTP → Controller → UseCase → MongoDB.
//  CÓMO: @SpringBootTest + MockMvc + Flapdoodle (MongoDB embebido).
//  MOCKS: ninguno — servicios no depende de módulos externos.
//    Todo es real: Spring context, use cases, repositorio, MongoDB en memoria.
//  ENDPOINTS cubiertos:
//    POST   /api/servicios              → crear servicio
//    GET    /api/servicios/{id}         → buscar por id
//    GET    /api/servicios/activos      → listar activos
//    GET    /api/servicios/{id}/activo  → verificar si está activo
//    PATCH  /api/servicios/{id}/activar → activar servicio
//    PATCH  /api/servicios/{id}/desactivar → desactivar servicio
// ─────────────────────────────────────────────────────────────────────────────

import com.belleza_app.servicios.domain.entities.Servicio;
import com.belleza_app.servicios.infraestructure.adaptors.secondary.persistence.ServicioMongoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ServicioController — integración (HTTP → UseCase → MongoDB)")
class ServicioControllerIntegrationTest {

    @Autowired MockMvc                mockMvc;
    @Autowired ObjectMapper           objectMapper;
    @Autowired ServicioMongoRepository mongoRepo;   // MongoDB real (Flapdoodle)

    @BeforeEach
    void setUp() {
        mongoRepo.deleteAll();
    }

    // ── POST /api/servicios ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/servicios → 201 y estado ACTIVO")
    void post_retorna201_conEstadoActivo() throws Exception {
        Servicio s = new Servicio("Manicure", "Manicure completo", 30000, 45);

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(s)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Manicure")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.id", notNullValue()));

        assertThat(mongoRepo.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/servicios → 422 si duración fuera de rango")
    void post_retorna422_siDuracionInvalida() throws Exception {
        Servicio s = new Servicio("Test", "desc", 10000, 600); // 600 min > 480

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(s)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /api/servicios → 422 si precio es cero")
    void post_retorna422_siPrecioCero() throws Exception {
        Servicio s = new Servicio("Test", "desc", 0, 60);

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(s)))
                .andExpect(status().isUnprocessableEntity());
    }

    // ── GET /api/servicios/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /{id} → 200 con datos correctos")
    void getById_retorna200_conDatos() throws Exception {
        String id = crearServicio("Pedicure", "Pedicure clásico", 35000, 60);

        mockMvc.perform(get("/api/servicios/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Pedicure")))
                .andExpect(jsonPath("$.id", is(id)));
    }

    @Test
    @DisplayName("GET /{id} → 404 si el servicio no existe")
    void getById_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(get("/api/servicios/id-fantasma"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje", containsString("id-fantasma")));
    }

    // ── GET /api/servicios/activos ────────────────────────────────────────────

    @Test
    @DisplayName("GET /activos → devuelve solo los servicios ACTIVOS")
    void getActivos_soloDevuelveActivos() throws Exception {
        // Guardar directamente en Mongo para controlar el estado
        guardarEnMongo("Corte", 20000, 30, Servicio.EstadoServicio.ACTIVO);
        guardarEnMongo("Masaje", 50000, 90, Servicio.EstadoServicio.INACTIVO);

        mockMvc.perform(get("/api/servicios/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado", is("ACTIVO")));
    }

    @Test
    @DisplayName("GET /activos → devuelve lista vacía si no hay activos")
    void getActivos_listaVacia_sinActivos() throws Exception {
        mockMvc.perform(get("/api/servicios/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── GET /api/servicios/{id}/activo ────────────────────────────────────────

    @Test
    @DisplayName("GET /{id}/activo → true cuando el servicio está ACTIVO")
    void esActivo_retornaTrue_cuandoActivo() throws Exception {
        String id = crearServicio("Depilación", "desc", 45000, 30);

        mockMvc.perform(get("/api/servicios/" + id + "/activo"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /{id}/activo → false cuando el servicio está INACTIVO")
    void esActivo_retornaFalse_cuandoInactivo() throws Exception {
        String id = guardarEnMongo("Masaje", 50000, 90, Servicio.EstadoServicio.INACTIVO);

        mockMvc.perform(get("/api/servicios/" + id + "/activo"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ── PATCH /api/servicios/{id}/desactivar ──────────────────────────────────

    @Test
    @DisplayName("PATCH /{id}/desactivar → 200 y estado INACTIVO")
    void desactivar_retorna200_estadoInactivo() throws Exception {
        String id = crearServicio("Tinte", "Tinte completo", 80000, 120);

        mockMvc.perform(patch("/api/servicios/" + id + "/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("INACTIVO")));
    }

    @Test
    @DisplayName("PATCH /{id}/desactivar → 404 si el servicio no existe")
    void desactivar_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(patch("/api/servicios/id-fantasma/desactivar"))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /api/servicios/{id}/activar ─────────────────────────────────────

    @Test
    @DisplayName("PATCH /{id}/activar → 200 y estado ACTIVO")
    void activar_retorna200_estadoActivo() throws Exception {
        String id = guardarEnMongo("Facial", 60000, 75, Servicio.EstadoServicio.INACTIVO);

        mockMvc.perform(patch("/api/servicios/" + id + "/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("ACTIVO")));
    }

    @Test
    @DisplayName("PATCH /{id}/activar → 404 si el servicio no existe")
    void activar_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(patch("/api/servicios/id-fantasma/activar"))
                .andExpect(status().isNotFound());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /** Crea un servicio vía HTTP y devuelve el id generado por MongoDB. */
    private String crearServicio(String nombre, String desc, double precio,
                                  int minutos) throws Exception {
        Servicio s = new Servicio(nombre, desc, precio, minutos);
        String resp = mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(s)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("id").asText();
    }

    /**
     * Inserta directamente en MongoDB con un estado concreto.
     * Útil para tests que necesitan un servicio INACTIVO de partida.
     */
    private String guardarEnMongo(String nombre, double precio, int minutos,
                                   Servicio.EstadoServicio estado) {
        com.belleza_app.servicios.infraestructure.adaptors.secondary.persistence.ServicioDocument doc =
                new com.belleza_app.servicios.infraestructure.adaptors.secondary.persistence.ServicioDocument();
        doc.setNombre(nombre);
        doc.setPrecio(precio);
        doc.setDuracionMinutos(minutos);
        doc.setEstado(estado.name());
        return mongoRepo.save(doc).getId();
    }
}