package com.simba.ReactiveClientDemo.exception.handler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Throwable
     */
    @ExceptionHandler(Throwable.class)
    public Object handleServerError(HttpServletRequest req, Exception ex) {
        log.error("Throwable Error!!! uri: {}, trace: {}", req.getRequestURI(), ExceptionUtils.getStackTrace(ex));

        return new ResponseEntity<>(ex, getDefaultHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * ReadTimeoutException
     */
    @ExceptionHandler(ReadTimeoutException.class)
    public HttpEntity<Object> handleReadTimeoutException(HttpServletRequest req, HttpServletResponse res, ReadTimeoutException ex) {
        return new HttpEntity<>(ex, getDefaultHttpHeaders());
    }

    private HttpHeaders getDefaultHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        MediaType mediaType = new MediaType(MediaType.APPLICATION_JSON_UTF8, Charset.forName("UTF-8"));
        httpHeaders.setContentType(mediaType);
        return httpHeaders;
    }

}