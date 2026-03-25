package com.belleza_app.citas.service;

import com.belleza_app.citas.dto.CitaRequest;
import com.belleza_app.citas.exception.CitaConflictoException;
import com.belleza_app.citas.exception.CitaNotFoundException;
import com.belleza_app.citas.exception.ReglaNegocioCitaException;
import com.belleza_app.citas.exception.ServicioInactivoException;
import com.belleza_app.citas.model.Cita;
import com.belleza_app.citas.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaService {

    private final CitaRepository repository;
    private final RestTemplate restTemplate;

    @Value("${servicios-service.url}")
    private String serviciosServiceUrl;

    @Value("${inventario-service.url}")
    private String inventarioServiceUrl;

    public CitaService(CitaRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public Cita crearCita(CitaRequest request) {
        validarFechaPosterior(request.getFechaHora());
        validarConflictoHorario(request.getClienteNombre(), request.getFechaHora());

        Cita cita = new Cita();
        cita.setClienteNombre(request.getClienteNombre());
        cita.setServicioId(request.getServicioId());
        cita.setProductoId(request.getProductoId());
        cita.setFechaHora(request.getFechaHora());
        cita.setEstado(Cita.EstadoCita.PENDIENTE);

        return repository.save(cita);
    }

    public Cita confirmarCita(String id) {
        Cita cita = buscarPorId(id);

        if (cita.getEstado() == Cita.EstadoCita.CANCELADA) {
            throw new ReglaNegocioCitaException("No se puede confirmar una cita cancelada");
        }

        boolean servicioActivo = consultarServicioActivo(cita.getServicioId());
        if (!servicioActivo) {
            throw new ServicioInactivoException(cita.getServicioId());
        }

        boolean inventarioDisponible = consultarInventarioDisponible(cita.getProductoId(), 1);
        if (!inventarioDisponible) {
            throw new ReglaNegocioCitaException("No hay stock suficiente para confirmar la cita");
        }

        cita.setEstado(Cita.EstadoCita.CONFIRMADA);
        return repository.save(cita);
    }

    public Cita cancelarCita(String id) {
        Cita cita = buscarPorId(id);
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        return repository.save(cita);
    }

    public Cita buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new CitaNotFoundException(id));
    }

    public List<Cita> listarPorCliente(String clienteNombre) {
        return repository.findByClienteNombre(clienteNombre);
    }

    private void validarFechaPosterior(LocalDateTime fechaHora) {
        if (fechaHora == null || !fechaHora.isAfter(LocalDateTime.now())) {
            throw new ReglaNegocioCitaException(
                    "La fecha de la cita debe ser posterior a la fecha actual"
            );
        }
    }

    private void validarConflictoHorario(String clienteNombre, LocalDateTime fechaHora) {
        repository.findByClienteNombreAndFechaHora(clienteNombre, fechaHora)
                .ifPresent(c -> {
                    throw new CitaConflictoException(
                            "El cliente ya tiene otra cita en el mismo horario"
                    );
                });
    }

    private boolean consultarServicioActivo(String servicioId) {
        String url = serviciosServiceUrl + "/api/servicios/" + servicioId + "/activo";
        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
        return Boolean.TRUE.equals(response.getBody());
    }

    private boolean consultarInventarioDisponible(String productoId, int cantidad) {
        String url = inventarioServiceUrl + "/api/productos/" + productoId + "/disponible?cantidad=" + cantidad;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String body = response.getBody();
        return body != null && body.contains("\"disponible\":true");
    }
}