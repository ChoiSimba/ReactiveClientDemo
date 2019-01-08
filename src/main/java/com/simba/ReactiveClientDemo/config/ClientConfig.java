package com.simba.ReactiveClientDemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
public class ClientConfig {
    private WebClientProperties webClientProperties;


    @Autowired
    public ClientConfig(WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
    }

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
     * Timeout설정을 아래와 같이 별도로 구현 가능.
     */
    @Bean
    public WebClient getWebClient() {
        int connectTimeout = (int) webClientProperties.getConnectTimeout().toMillis();
        long readTimeout = webClientProperties.getReadTimeout().toMillis();
        long writeTimeout = webClientProperties.getWriteTimeout().toMillis();

        log.info("connectTimeout: {}ms, readTimeout: {}ms, writeTimeout: {}ms", connectTimeout, readTimeout, writeTimeout);

        Function<HttpClient, HttpClient> mapper = httpClient -> httpClient
                .tcpConfiguration(client -> client
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                        .doOnConnected(connection -> connection
                                .addHandler(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                                .addHandler(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                        )
                );

        ClientHttpConnector clientHttpConnector = new ReactorClientHttpConnector(reactorResourceFactory(), mapper);

        return WebClient.builder()
                .baseUrl(webClientProperties.getBaseUrl())
                .clientConnector(clientHttpConnector)
                .build();
    }
}
