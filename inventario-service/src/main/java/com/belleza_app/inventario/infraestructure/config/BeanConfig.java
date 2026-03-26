package com.belleza_app.inventario.infraestructure.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.belleza_app.inventario.application.usecase.AgregarStockUseCase;
import com.belleza_app.inventario.application.usecase.ConsultarStockUseCase;
import com.belleza_app.inventario.application.usecase.DescontarStockUseCase;
import com.belleza_app.inventario.application.usecase.HayStockDisponibleUseCase;
import com.belleza_app.inventario.application.usecase.RegistrarProductoUseCase;
import com.belleza_app.inventario.domain.ports.in.AgregarStockEntrada;
import com.belleza_app.inventario.domain.ports.in.ConsultarStockEntrada;
import com.belleza_app.inventario.domain.ports.in.DescontarStockEntrada;
import com.belleza_app.inventario.domain.ports.in.HayStockDisponibleEntrada;
import com.belleza_app.inventario.domain.ports.in.RegistrarProductoEntrada;
import com.belleza_app.inventario.domain.ports.out.ProductoRepositorioPuerto;


@Configuration
public class BeanConfig {

    @Bean
    public RegistrarProductoEntrada registrarProductoUseCase(
            ProductoRepositorioPuerto repo) {
        return new RegistrarProductoUseCase(repo);
    }

    @Bean
    public ConsultarStockEntrada consultarStockUseCase(
            ProductoRepositorioPuerto repo) {
        return new ConsultarStockUseCase(repo);
    }

    @Bean
    public AgregarStockEntrada agregarStockUseCase(
            ProductoRepositorioPuerto repo) {
        return new AgregarStockUseCase(repo);
    }

    @Bean
    public DescontarStockEntrada descontarStockUseCase(
            ProductoRepositorioPuerto repo) {
        return new DescontarStockUseCase(repo);
    }

    @Bean
    public HayStockDisponibleEntrada hayStockDisponibleUseCase(
            ProductoRepositorioPuerto repo) {
        return new HayStockDisponibleUseCase(repo);
    }
}