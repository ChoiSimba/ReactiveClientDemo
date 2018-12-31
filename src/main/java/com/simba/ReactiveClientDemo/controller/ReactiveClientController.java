package com.simba.ReactiveClientDemo.controller;


import com.simba.ReactiveClientDemo.model.Book;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ReactiveClientController {

    private WebClient webClient;


    public ReactiveClientController() {
        webClient = WebClient.create("http://localhost:8080");
    }

    @GetMapping("/book/{bookIndex}")
    public Mono<Book> getBook(@PathVariable("bookIndex") int bookIndex) {
        Mono<Book> resultMono = webClient.get()
                .uri("/book/" + bookIndex)
                .retrieve()
                .bodyToMono(Book.class)
                .log()
                ;

        log.info("@@@@@ after webClient");

        return resultMono;
    }

    @GetMapping("/bookBlocking/{environment}/{bookIndex}")
    public Object getBookBlocking(@PathVariable("environment") String environment, @PathVariable("bookIndex") int bookIndex) {
        boolean isServer = "server".equals(environment);
        String requestUri = String.format("/book%s/%d", isServer ? "Blocking" : "", bookIndex);

        Object resultObject = null;

        if (isServer) {
            resultObject = webClient.get()
                    .uri(requestUri)
                    .retrieve()
                    .bodyToMono(Book.class)
                    .log()
            ;
        } else {
            resultObject = webClient.get()
                    .uri(requestUri)
                    .retrieve()
                    .bodyToMono(Book.class)
                    .log()
                    .block()
            ;

            /*
            resultObject = webClient.get()
                    .uri(requestUri)
                    .retrieve()
                    .bodyToMono(Book.class)
                    .log()
            ;

            ((Mono) resultObject).block();
            */
        }

        log.info("@@@@@ after webClient");

        return resultObject;
    }

    @GetMapping("/books")
    public Flux<String> books() {
        Flux<String> resultFlux = webClient.get()
                .uri("/books")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .log()
                ;

        log.info("@@@@@ after webClient");

        return resultFlux;
    }

}
