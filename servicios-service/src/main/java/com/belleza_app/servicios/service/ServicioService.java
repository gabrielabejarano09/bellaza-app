package com.belleza_app.servicios.service;

import com.belleza_app.servicios.exception.ReglaNegocioServicioException;
import com.belleza_app.servicios.exception.ServicioNotFoundException;
import com.belleza_app.servicios.model.Servicio;
import com.belleza_app.servicios.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Capa de lógica de negocio para el módulo de Servicios.
 * Aquí se validan TODAS las reglas de negocio antes de persistir datos.
 */
@Service
public class ServicioService {

    private final ServicioRepository repository;

    // Inyección por constructor (recomendada sobre @Autowired)
    public ServicioService(ServicioRepository repository) {
        this.repository = repository;
    }

    // ─── Crear Servicio ──────────────────────────────────────────────────────

    /**
     * Caso de uso: Crear servicio.
     * Reglas de negocio aplicadas:
     *   - El precio no puede ser cero ni negativo.
     *   - La duración debe estar entre 15 y 480 minutos.
     */
    public Servicio crearServicio(Servicio servicio) {
        validarPrecio(servicio.getPrecio());
        validarDuracion(servicio.getDuracionMinutos());

        // Todo servicio nuevo nace ACTIVO
        servicio.setEstado(Servicio.EstadoServicio.ACTIVO);

        return repository.save(servicio);
    }

    // ─── Activar / Desactivar ────────────────────────────────────────────────

    /**
     * Caso de uso: Activar servicio.
     */
    public Servicio activarServicio(String id) {
        Servicio servicio = buscarPorId(id);
        servicio.setEstado(Servicio.EstadoServicio.ACTIVO);
        return repository.save(servicio);
    }

    /**
     * Caso de uso: Desactivar servicio.
     */
    public Servicio desactivarServicio(String id) {
        Servicio servicio = buscarPorId(id);
        servicio.setEstado(Servicio.EstadoServicio.INACTIVO);
        return repository.save(servicio);
    }

    // ─── Consultas ───────────────────────────────────────────────────────────

    /**
     * Caso de uso: Consultar servicio por ID.
     */
    public Servicio buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ServicioNotFoundException(id));
    }

    /**
     * Caso de uso: Listar servicios activos.
     */
    public List<Servicio> listarActivos() {
        return repository.findByEstado(Servicio.EstadoServicio.ACTIVO);
    }

    /**
     * Consulta auxiliar usada por el módulo de Citas vía REST.
     * Retorna true si el servicio existe Y está ACTIVO.
     */
    public boolean esServicioActivo(String id) {
        return repository.findById(id)
                .map(s -> s.getEstado() == Servicio.EstadoServicio.ACTIVO)
                .orElse(false);
    }

    /**
     * Caso de uso: Eliminar servicio.
     * Regla de negocio: un servicio no puede eliminarse si tiene citas futuras.
     * NOTA: Esta validación la hace el módulo de Citas. Aquí simplemente eliminamos.
     * En producción se llamaría a citas-service antes de eliminar.
     */
    public void eliminarServicio(String id) {
        Servicio servicio = buscarPorId(id); // valida que exista
        repository.delete(servicio);
    }

    // ─── Validaciones privadas ───────────────────────────────────────────────

    /**
     * Regla de negocio: el precio no puede ser cero ni negativo.
     */
    private void validarPrecio(double precio) {
        if (precio <= 0) {
            throw new ReglaNegocioServicioException(
                "El precio del servicio debe ser mayor a cero. Precio recibido: " + precio);
        }
    }

    /**
     * Regla de negocio: la duración debe estar entre 15 y 480 minutos.
     */
    private void validarDuracion(int minutos) {
        if (minutos < 15 || minutos > 480) {
            throw new ReglaNegocioServicioException(
                "La duración debe estar entre 15 y 480 minutos. Duración recibida: " + minutos);
        }
    }
}