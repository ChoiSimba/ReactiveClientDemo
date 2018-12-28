package com.simba.ReactiveClientDemo.handler;

import com.simba.ReactiveClientDemo.model.Book;
import com.simba.ReactiveClientDemo.repository.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReactiveServerHandler {
    private final BookRepository bookRepository;


    @Autowired
    ReactiveServerHandler(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<ServerResponse> book(ServerRequest request) {
        int bookId = Integer.valueOf(request.pathVariable("bookId"));

        Mono<ServerResponse> responseMono = ServerResponse.ok().body(bookRepository.book(bookId), Book.class);
        log.info("@@@@@ after response");

        return responseMono;
    }

    public Mono<ServerResponse> books(ServerRequest request) {
        Mono<ServerResponse> responseMono = ServerResponse.ok().body(bookRepository.books(), Book.class);
        log.info("@@@@@ after response");

        return responseMono;
    }
}
