package ch.xavier.rest;

import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

//@Configuration
//@EnableWebFlux
public class CorsConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
//                .allowedOrigins("http://172.18.0.9")
                .allowedMethods("GET")
                .maxAge(3600);
    }
}