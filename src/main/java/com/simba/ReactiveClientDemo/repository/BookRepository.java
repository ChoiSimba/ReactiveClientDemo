package com.simba.ReactiveClientDemo.repository;

import com.simba.ReactiveClientDemo.model.Book;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class BookRepository {
    private final WebClient webClient;


    BookRepository() {
        webClient = WebClient.create("http://localhost:8080");
    }

    public Mono<Book> book(int bookId) {
        return webClient.get()
                .uri("/book/" + bookId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception("4xx Error")))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception("5xx Error")))
                .bodyToMono(Book.class)
                .log()
                ;
    }

    public Flux<Book> books() {
        return webClient.get()
                .uri("/books")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception("4xx Error")))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception("5xx Error")))
                .bodyToFlux(Book.class)
                .log()
                ;
    }
}
