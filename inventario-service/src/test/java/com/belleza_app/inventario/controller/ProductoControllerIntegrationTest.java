package com.belleza_app.inventario.controller;

import com.belleza_app.inventario.model.Producto;
import com.belleza_app.inventario.repository.ProductoRepository;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/productos crea producto")
    void crearProducto() throws Exception {
        Producto producto = new Producto("Cera", "Cera capilar", 15);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Cera")));
    }

    @Test
    @DisplayName("GET /api/productos/{id} retorna producto")
    void buscarPorId() throws Exception {
        Producto guardado = repository.save(new Producto("Spray", "Spray fijador", 8));

        mockMvc.perform(get("/api/productos/{id}", guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Spray")));
    }

    @Test
    @DisplayName("PATCH /api/productos/{id}/descontar-stock descuenta")
    void descontarStock() throws Exception {
        Producto guardado = repository.save(new Producto("Aceite", "Aceite para barba", 10));

        String body = """
                {
                  "cantidad": 4
                }
                """;

        mockMvc.perform(patch("/api/productos/{id}/descontar-stock", guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock", is(6)));
    }
}