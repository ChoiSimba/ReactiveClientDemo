package com.simba.ReactiveClientDemo.config;

import com.simba.ReactiveClientDemo.handler.ReactiveServerHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
    private final ReactiveServerHandler reactiveServerHandler;


    @Autowired
    WebConfig(ReactiveServerHandler reactiveServerHandler) {
        this.reactiveServerHandler = reactiveServerHandler;
    }

    @Bean
    public RouterFunction<?> routeBook() {
        return route(GET("/book/{bookId}"), reactiveServerHandler::book);
    }

    @Bean
    public RouterFunction<?> routeBooks() {
        return route(GET("/books"), reactiveServerHandler::books);
    }
}

