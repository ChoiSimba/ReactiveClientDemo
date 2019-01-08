package com.simba.ReactiveClientDemo.controller;


import com.simba.ReactiveClientDemo.model.Book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * WebClient 동기/비동기 테스트 컨트롤러
 * log를 기준으로 WebClient 호출 후 로그가 찍히는 시점을 기준으로
 * 동기, 비동기가 정상적으로 수행되는지를 테스트.
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
     * Mono 동기 테스트
     */
    @GetMapping("/mono/sync/{serverDelayMs}")
    public Book getMonoSync(@PathVariable("serverDelayMs") int serverDelayMs,
                            @RequestParam(value = "serverSync", required = false) String serverSync
    ) {
        String requestUri = String.format("/mono/%s/%d", "sync".equals(serverSync) ? "sync" : "async", serverDelayMs);

        Book book = webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(Book.class)
                .log()
                .doOnSuccess(onSuccess -> log.info("onSuccess"))
                .block();

        log.info("webClient was executed");

        return book;
    }

    /**
     * Mono 비동기 테스트
     */
    @GetMapping("/mono/async/{serverDelayMs}")
    public Mono<Book> getMonoAsync(@PathVariable("serverDelayMs") int serverDelayMs,
                                   @RequestParam(value = "serverSync", required = false) String serverSync
    ) {
        String requestUri = String.format("/mono/%s/%d", "sync".equals(serverSync) ? "sync" : "async", serverDelayMs);

        Mono<Book> bookMono = webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(Book.class)
                .log()
                .doOnSuccess(onSuccess -> log.info("onSuccess"))
                ;

        log.info("webClient was executed");

        return bookMono;
    }

    /**
     * Flux 테스트
     * stream으로 각 요소들을 정상적으로 가져오는지 테스트.
     */
    @GetMapping("/flux")
    public Flux<String> getFlux() {
        Flux<String> resultFlux = webClient.get()
                .uri("/flux")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .log()
                ;

        log.info("webClient was executed");

        return resultFlux;
    }

    /**
     * Gzip 테스트
     * Gzip으로 압축된 내용을 가져와서 잘 압축이 풀리는지 테스트.
     */
    @GetMapping("/gzipContent/{delayMs}")
    public ResponseEntity<Mono<byte[]>> getGzipContent(@PathVariable("delayMs") int delayMs) {
        Mono<byte[]> resultMono = webClient.get()
                .uri("/gzipContent/" + delayMs)
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray)
                .log()
                .doOnSuccess(onSuccess -> log.info("origin gzip content: {}", new String(onSuccess)))
                ;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_ENCODING, "gzip")
                .body(resultMono);
    }
}
