package com.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class HedgingSimulationApplication {

    public static void main(String[] args) {
        SpringApplication.run(HedgingSimulationApplication.class, args);
    }

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        return new TomcatServletWebServerFactory(8080); // 0 means random port
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        if (event.getApplicationContext() instanceof ServletWebServerApplicationContext) {
            ServletWebServerApplicationContext serverCtx = (ServletWebServerApplicationContext) event.getApplicationContext();
            System.out.println("Application is running on port: " + serverCtx.getWebServer().getPort());
        } else {
            System.out.println("Not a web application context");
        }
    }
}