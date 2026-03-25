package com.belleza_app.servicios.controller;

import com.belleza_app.servicios.model.Servicio;
import com.belleza_app.servicios.repository.ServicioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * INTEGRATION TESTS para ServicioController.
 *
 * Qué son integration tests (lo que pide el profe):
 * - Prueban el BLOQUE COMPLETO: Controller → Service → Repository → MongoDB.
 * - @SpringBootTest levanta todo el contexto de Spring (como si fuera producción).
 * - Usa MongoDB EMBEBIDO (flapdoodle) para no necesitar Mongo instalado.
 * - MockMvc simula peticiones HTTP reales sin levantar el servidor en un puerto.
 * - Muy pocos o ningún Mock — todo es real.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ServicioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // simula llamadas HTTP

    @Autowired
    private ServicioRepository repository; // repositorio real con Mongo embebido

    @Autowired
    private ObjectMapper objectMapper; // convierte objetos a JSON

    @BeforeEach
    void limpiarBD() {
        // Antes de cada test limpiamos la colección para no tener datos sucios
        repository.deleteAll();
    }

    // ─── POST /api/servicios ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST servicio válido → 201 Created con estado ACTIVO")
    void crearServicio_valido_retorna201() throws Exception {
        Servicio nuevo = new Servicio("Manicure", "Manicure completo", 30000, 45);

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Manicure"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("POST servicio con duración fuera de rango → 422")
    void crearServicio_duracionInvalida_retorna422() throws Exception {
        Servicio invalido = new Servicio("Test", "desc", 10000, 600);

        mockMvc.perform(post("/api/servicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isUnprocessableEntity());
    }

    // ─── GET /api/servicios/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("GET servicio existente → 200 con datos correctos")
    void buscarPorId_existente_retorna200() throws Exception {
        Servicio guardado = repository.save(
                new Servicio("Pedicure", "Pedicure clásico", 35000, 60));

        mockMvc.perform(get("/api/servicios/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pedicure"));
    }

    @Test
    @DisplayName("GET servicio inexistente → 404 Not Found")
    void buscarPorId_noExistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/servicios/id-que-no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(containsString("id-que-no-existe")));
    }

    // ─── GET /api/servicios/activos ──────────────────────────────────────────

    @Test
    @DisplayName("GET activos → solo retorna servicios ACTIVOS")
    void listarActivos_soloRetornaActivos() throws Exception {
        Servicio activo = new Servicio("Corte", "desc", 20000, 30);
        activo.setEstado(Servicio.EstadoServicio.ACTIVO);

        Servicio inactivo = new Servicio("Masaje", "desc", 50000, 90);
        inactivo.setEstado(Servicio.EstadoServicio.INACTIVO);

        repository.save(activo);
        repository.save(inactivo);

        mockMvc.perform(get("/api/servicios/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    // ─── PATCH activar / desactivar ──────────────────────────────────────────

    @Test
    @DisplayName("PATCH desactivar → servicio pasa a INACTIVO")
    void desactivar_retornaInactivo() throws Exception {
        Servicio activo = repository.save(
                new Servicio("Tinte", "Tinte completo", 80000, 120));

        mockMvc.perform(patch("/api/servicios/" + activo.getId() + "/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("INACTIVO"));
    }

    @Test
    @DisplayName("PATCH activar servicio inactivo → pasa a ACTIVO")
    void activar_retornaActivo() throws Exception {
        Servicio inactivo = new Servicio("Facial", "desc", 60000, 75);
        inactivo.setEstado(Servicio.EstadoServicio.INACTIVO);
        inactivo = repository.save(inactivo);

        mockMvc.perform(patch("/api/servicios/" + inactivo.getId() + "/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    // ─── GET /{id}/activo ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /{id}/activo con servicio activo → retorna true")
    void esActivo_servicioActivo_retornaTrue() throws Exception {
        Servicio activo = repository.save(
                new Servicio("Depilación", "desc", 45000, 30));

        mockMvc.perform(get("/api/servicios/" + activo.getId() + "/activo"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}