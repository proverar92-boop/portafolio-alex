package com.gianmeza.portafolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ProyectoAplicacionProfesionalApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ProyectoAplicacionProfesionalApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ProyectoAplicacionProfesionalApplication.class, args);
    }
}
