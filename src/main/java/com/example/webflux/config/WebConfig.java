package com.example.webflux.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
    
    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route(GET("/"), 
            request -> ServerResponse.permanentRedirect(java.net.URI.create("/h2-console")).build());
    }
}
