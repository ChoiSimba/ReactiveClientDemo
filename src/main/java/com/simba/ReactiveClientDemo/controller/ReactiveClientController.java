package com.simba.ReactiveClientDemo.controller;


import com.simba.ReactiveClientDemo.model.Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * WebClient 동기/비동기 테스트 컨트롤러
 * log를 기준으로 WebClient 호출 후 'after webClient'로그가 찍히는 시점을 기준으로
 * 동기, 비동기가 정상적으로 수행되는지를 테스트.
 * 비동기 실험을 위해 호출하는 서버의 end-point에 일정 delay를 적용하였음.
 */
@RestController
@Slf4j
public class ReactiveClientController {

    private WebClient webClient;


    @Autowired
    public ReactiveClientController(WebClient webClient) {
        this.webClient = webClient;
    }


    /**
     * Mono 비동기 테스트
     * WebClient 호출 후 log.info가 바로 수행되고, 그 다음 WebClient의 응답이 로깅된다.
     */
    @GetMapping("/bookAsync/{bookIndex}")
    public Mono<Book> getBookAsync(@PathVariable("bookIndex") int bookIndex) {
        Mono<Book> resultMono = webClient.get()
                .uri("/bookAsync/" + bookIndex)
                .retrieve()
                .bodyToMono(Book.class)
                .log()
                ;

        log.info("@@@@@ after webClient");

        return resultMono;
    }

    /**
     * Mono 동기 테스트
     * path에 Server동기, Client동기를 선택하여 테스트 할 수 있음.
     * 서버와 클라이언트가 동기/비동기에 대한 구현이 일치하지 않을 경우 (ex:클라이언트 비동기, 서버 동기)
     *   ㄴ /bookSync/Server/? : Server동기, Client 비동기
     *   ㄴ /bookSync/Client/? : Server비동기, Client 동기
     * 정상적으로 호출되는 지에 대한 테스트.
     */
    @GetMapping("/bookSync/{environment}/{bookIndex}")
    public Object getBookSync(@PathVariable("environment") String environment, @PathVariable("bookIndex") int bookIndex) {
        boolean isServer = "server".equals(environment);
        String requestUri = String.format("/book%s/%d", isServer ? "Sync" : "Async", bookIndex);

        Object resultObject;

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
        }

        log.info("@@@@@ after webClient");

        return resultObject;
    }

    /**
     * Flux 테스트
     * stream으로 각 요소들을 정상적으로 가져오는지 테스트.
     */
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
