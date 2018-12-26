package com.simba.ReactiveClientDemo.controller;


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
    public Mono<String> getBook(@PathVariable("bookIndex") int bookIndex) {
        Mono<String> resultMono = webClient.get()
                .uri("/book/" + bookIndex)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(book -> log.info("@@@@@ onNext: {}", book))
                .doOnSuccessOrError((book, throwable) -> log.info("@@@@@ OnSuccessOrError: {}, {}", book, throwable))
                .log()
                ;

        log.info("@@@@@ after webClient");

        return resultMono;
    }

    @GetMapping("/books")
    public Flux<String> books() {
        Flux<String> resultFlux = webClient.get()
                .uri("/books")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(book -> log.info("@@@@@ onNext: {}", book))
                .doOnComplete(() -> log.info("@@@@@ onComplete: {}"))
                .log()
                ;

        log.info("@@@@@ after webClient");

        return resultFlux;
    }

}
