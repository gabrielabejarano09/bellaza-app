package com.belleza_app.servicios.infraestructure.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.belleza_app.servicios.application.usecase.ActivarServicioUseCase;
import com.belleza_app.servicios.application.usecase.BuscarServicioUseCase;
import com.belleza_app.servicios.application.usecase.CrearServicioUseCase;
import com.belleza_app.servicios.application.usecase.DesactivarServicioUseCase;
import com.belleza_app.servicios.application.usecase.EsServicioActivoUseCase;
import com.belleza_app.servicios.application.usecase.ListarServiciosActivosUseCase;
import com.belleza_app.servicios.domain.ports.in.ActivarServicioEntrada;
import com.belleza_app.servicios.domain.ports.in.BuscarServicioEntrada;
import com.belleza_app.servicios.domain.ports.in.CrearServicioEntrada;
import com.belleza_app.servicios.domain.ports.in.DesactivarServicioEntrada;
import com.belleza_app.servicios.domain.ports.in.EsServicioActivoEntrada;
import com.belleza_app.servicios.domain.ports.in.ListarServiciosActivosEntrada;
import com.belleza_app.servicios.domain.ports.out.ServicioRepositorioPuerto;

@Configuration
public class BeanConfig {

    @Bean
    public CrearServicioEntrada crearServicioUseCase(
            ServicioRepositorioPuerto repo) {
        return new CrearServicioUseCase(repo);
    }

    @Bean
    public BuscarServicioEntrada buscarServicioUseCase(
            ServicioRepositorioPuerto repo) {
        return new BuscarServicioUseCase(repo);
    }

    @Bean
    public ListarServiciosActivosEntrada listarServiciosActivosUseCase(
            ServicioRepositorioPuerto repo) {
        return new ListarServiciosActivosUseCase(repo);
    }

    @Bean
    public ActivarServicioEntrada activarServicioUseCase(
            ServicioRepositorioPuerto repo) {
        return new ActivarServicioUseCase(repo);
    }

    @Bean
    public DesactivarServicioEntrada desactivarServicioUseCase(
            ServicioRepositorioPuerto repo) {
        return new DesactivarServicioUseCase(repo);
    }

    @Bean
    public EsServicioActivoEntrada esServicioActivoUseCase(
            ServicioRepositorioPuerto repo) {
        return new EsServicioActivoUseCase(repo);
    }

}