package com.example.circuitbreaker.web;

import com.example.circuitbreaker.model.Response;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class Resilience4jController {
  
  private static final String RESILIENCE4J_INSTANCE_NAME = "example";
  private static final String FALLBACK_METHOD = "fallback";
  
  @GetMapping(
      value = "/timeout/{timeout}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
  @TimeLimiter(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
  public Mono<Response<String>> timeout(@PathVariable int timeout) {
    return Mono.just(toOkResponse())
        .delayElement(Duration.ofSeconds(timeout));
  }

  @GetMapping(
      value = "/delay/{delay}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
  public Mono<Response<String>> delay(@PathVariable int delay) {
    return Mono.just(toOkResponse())
        .delayElement(Duration.ofSeconds(delay));
  }

  @GetMapping(
      value = "/response",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
  public Mono<Response<String>> error() {
    boolean valid;
    if (Math.random() > 0.3) {
      valid = false;
    } else {
      valid = true;
    }
    return Mono.just(valid)
        .flatMap(this::toOkResponse);
  }
  
  public Mono<Response<String>> fallback(Exception ex) {
    return Mono.just(toResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Service 1 Response: DOWN"))
        .doOnNext(result -> log.warn("fallback executed"));
  }
  
  private Mono<Response<String>> toOkResponse(boolean valid) {
    if (!valid) {
      return Mono.just(toOkResponse());
    }
    return Mono.error(new RuntimeException("error"));
  }
  
  private Response<String> toOkResponse() {
    return toResponse(HttpStatus.OK, "Service 1 Response: UP");
  }

  private Response<String> toResponse(HttpStatus httpStatus, String result) {
    return Response.<String>builder()
        .code(httpStatus.value())
        .status(httpStatus.getReasonPhrase())
        .data(result)
        .build();
  }
}
