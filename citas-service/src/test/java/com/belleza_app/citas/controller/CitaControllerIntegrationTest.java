package com.belleza_app.citas.controller;

import com.belleza_app.citas.dto.CitaRequest;
import com.belleza_app.citas.repository.CitaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CitaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CitaRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/citas crea cita")
    void crearCita() throws Exception {
        CitaRequest request = new CitaRequest();
        request.setClienteNombre("Gabriela");
        request.setServicioId("serv1");
        request.setProductoId("prod1");
        request.setFechaHora(LocalDateTime.now().plusDays(1));

        mockMvc.perform(post("/api/citas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteNombre", is("Gabriela")))
                .andExpect(jsonPath("$.estado", is("PENDIENTE")));
    }
}