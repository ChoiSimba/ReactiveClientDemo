package com.simba.ReactiveClientDemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Function;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class ClientConfig {

    /**
     * ReactorResourceFactory
     */
    @Bean
    public ReactorResourceFactory reactorResourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        return factory;
    }

    /**
     * WebClient
     * Timeout설정을 아래와 같이 별도로 구현할 수 있다.
     */
    @Bean
    public WebClient getWebClient() {
        Function<HttpClient, HttpClient> mapper = httpClient -> httpClient
                .create()
                .tcpConfiguration(client -> client
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 50000)
                        .doOnConnected(connection -> connection
                                .addHandler(new ReadTimeoutHandler(3))
                                .addHandler(new WriteTimeoutHandler(5))
                        )
                );
        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(reactorResourceFactory(), mapper);

        return WebClient.builder()
                .baseUrl("http://localhost:8080")
                .clientConnector(clientHttpConnector)
                .build();
    }
}
