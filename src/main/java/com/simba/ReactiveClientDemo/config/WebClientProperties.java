package com.simba.ReactiveClientDemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import lombok.Data;

/**
 * WebClient 설정정보
 *   Timeout은 Duration 객체로 받는다.
 */
@ConfigurationProperties(prefix = "web-client")
@Data
public class WebClientProperties {
    private String baseUrl;
    private Duration connectTimeout;
    private Duration readTimeout;
    private Duration writeTimeout;
}
