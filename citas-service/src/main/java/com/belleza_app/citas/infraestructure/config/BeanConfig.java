package com.belleza_app.citas.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.belleza_app.citas.application.usecase.AgendarCitaUseCase;
import com.belleza_app.citas.application.usecase.CancelarCitaUseCase;
import com.belleza_app.citas.application.usecase.ConfirmarCitaUseCase;
import com.belleza_app.citas.application.usecase.ConsultarCitaUseCase;
import com.belleza_app.citas.domain.ports.in.AgendarCitaEntrada;
import com.belleza_app.citas.domain.ports.in.CancelarCitaEntrada;
import com.belleza_app.citas.domain.ports.in.ConfirmarCitaEntrada;
import com.belleza_app.citas.domain.ports.in.ConsultarCitaEntrada;
import com.belleza_app.citas.domain.ports.out.CitaRepositorioPuerto;
import com.belleza_app.citas.domain.ports.out.InventarioClientePuerto;
import com.belleza_app.citas.domain.ports.out.ServicioClientePuerto;

@Configuration
public class BeanConfig {


    @Bean
    public AgendarCitaEntrada agendarCitaUseCase(
            CitaRepositorioPuerto citaRepo,
            ServicioClientePuerto servicioCliente) {
        return new AgendarCitaUseCase(citaRepo, servicioCliente);
    }

    @Bean
    public ConfirmarCitaEntrada confirmarCitaUseCase(
            CitaRepositorioPuerto citaRepo,
            InventarioClientePuerto inventarioCliente) {
        return new ConfirmarCitaUseCase(citaRepo, inventarioCliente);
    }

    @Bean
    public CancelarCitaEntrada cancelarCitaUseCase(
            CitaRepositorioPuerto citaRepo) {
        return new CancelarCitaUseCase(citaRepo);
    }

    @Bean
    public ConsultarCitaEntrada consultarCitaUseCase(
            CitaRepositorioPuerto citaRepo) {
        return new ConsultarCitaUseCase(citaRepo);
    }
}
