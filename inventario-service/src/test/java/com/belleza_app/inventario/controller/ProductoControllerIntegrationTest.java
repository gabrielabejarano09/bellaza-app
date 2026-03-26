package com.belleza_app.inventario.controller;

// ─────────────────────────────────────────────────────────────────────────────
//  PRUEBA DE INTEGRACIÓN — ProductoController
//
//  QUÉ prueba: el flujo completo HTTP → Controller → UseCase → MongoDB.
//  CÓMO: @SpringBootTest + MockMvc + Flapdoodle (MongoDB embebido).
//  MOCKS: ninguno — inventario no depende de servicios externos.
//    Todo es real: Spring context, use cases, repositorio, MongoDB en memoria.
//  ENDPOINTS cubiertos:
//    POST  /api/productos                    → registrar producto
//    GET   /api/productos/{id}/stock         → consultar stock
//    PATCH /api/productos/{id}/agregar-stock → agregar stock
//    PATCH /api/productos/{id}/descontar-stock→ descontar stock
//    GET   /api/productos/{id}/disponible    → verificar disponibilidad
// ─────────────────────────────────────────────────────────────────────────────

import com.belleza_app.inventario.domain.entities.Producto;
import com.belleza_app.inventario.infraestructure.adaptors.secondary.persistence.ProductoMongoRepository;
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
@DisplayName("ProductoController — integración (HTTP → UseCase → MongoDB)")
class ProductoControllerIntegrationTest {

    @Autowired MockMvc               mockMvc;
    @Autowired ObjectMapper          objectMapper;
    @Autowired ProductoMongoRepository mongoRepo;   // MongoDB real (Flapdoodle)

    @BeforeEach
    void setUp() {
        mongoRepo.deleteAll();
    }

    // ── POST /api/productos ───────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/productos → 201 y persiste en MongoDB")
    void post_retorna201_yPersiste() throws Exception {
        Producto p = new Producto("Cera", "Cera capilar", 15);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Cera")))
                .andExpect(jsonPath("$.stock", is(15)))
                .andExpect(jsonPath("$.id", notNullValue()));

        assertThat(mongoRepo.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/productos con stock 0 → 201 (stock cero es válido al registrar)")
    void post_conStockCero_retorna201() throws Exception {
        Producto p = new Producto("Aceite", "Aceite de argán", 0);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(p)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stock", is(0)));
    }

    // ── GET /api/productos/{id}/stock ─────────────────────────────────────────

    @Test
    @DisplayName("GET /{id}/stock → 200 con datos de stock correctos")
    void getStock_retorna200_conDatos() throws Exception {
        String id = registrarProducto("Spray", "Fijador", 8);

        mockMvc.perform(get("/api/productos/" + id + "/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockActual", is(8)))
                .andExpect(jsonPath("$.disponible", is(true)))
                .andExpect(jsonPath("$.nombreProducto", is("Spray")));
    }

    @Test
    @DisplayName("GET /{id}/stock → 404 si el producto no existe")
    void getStock_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(get("/api/productos/id-fantasma/stock"))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /api/productos/{id}/agregar-stock ───────────────────────────────

    @Test
    @DisplayName("PATCH /{id}/agregar-stock → 200 y suma al stock existente")
    void agregarStock_retorna200_sumaStock() throws Exception {
        String id = registrarProducto("Shampoo", "Profesional", 10);

        mockMvc.perform(patch("/api/productos/" + id + "/agregar-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock", is(15)));
    }

    @Test
    @DisplayName("PATCH /{id}/agregar-stock → 404 si el producto no existe")
    void agregarStock_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(patch("/api/productos/id-fantasma/agregar-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 5}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /{id}/agregar-stock → 400 si cantidad es 0")
    void agregarStock_retorna400_siCantidadCero() throws Exception {
        String id = registrarProducto("Cera", "Capilar", 5);

        mockMvc.perform(patch("/api/productos/" + id + "/agregar-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 0}"))
                .andExpect(status().isBadRequest());
    }

    // ── PATCH /api/productos/{id}/descontar-stock ─────────────────────────────

    @Test
    @DisplayName("PATCH /{id}/descontar-stock → 200 y resta del stock")
    void descontarStock_retorna200_restaStock() throws Exception {
        String id = registrarProducto("Aceite", "Capilar", 10);

        mockMvc.perform(patch("/api/productos/" + id + "/descontar-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock", is(6)));
    }

    @Test
    @DisplayName("PATCH /{id}/descontar-stock → 422 si no hay stock suficiente")
    void descontarStock_retorna422_siInsuficiente() throws Exception {
        String id = registrarProducto("Mascarilla", "Hidratante", 3);

        mockMvc.perform(patch("/api/productos/" + id + "/descontar-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 10}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("PATCH /{id}/descontar-stock → 404 si el producto no existe")
    void descontarStock_retorna404_siNoExiste() throws Exception {
        mockMvc.perform(patch("/api/productos/id-fantasma/descontar-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cantidad\": 1}"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/productos/{id}/disponible ────────────────────────────────────

    @Test
    @DisplayName("GET /{id}/disponible → true cuando hay stock suficiente")
    void disponible_retornaTrue_conStock() throws Exception {
        String id = registrarProducto("Tónico", "Capilar", 10);

        mockMvc.perform(get("/api/productos/" + id + "/disponible")
                        .param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /{id}/disponible → false cuando no hay stock suficiente")
    void disponible_retornaFalse_sinStock() throws Exception {
        String id = registrarProducto("Crema", "Facial", 2);

        mockMvc.perform(get("/api/productos/" + id + "/disponible")
                        .param("cantidad", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("GET /{id}/disponible → false cuando el producto no existe")
    void disponible_retornaFalse_siNoExiste() throws Exception {
        mockMvc.perform(get("/api/productos/id-fantasma/disponible")
                        .param("cantidad", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /** Registra un producto vía HTTP y devuelve el id generado por MongoDB. */
    private String registrarProducto(String nombre, String desc, int stock) throws Exception {
        Producto p = new Producto(nombre, desc, stock);
        String resp = mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(p)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resp).get("id").asText();
    }
}