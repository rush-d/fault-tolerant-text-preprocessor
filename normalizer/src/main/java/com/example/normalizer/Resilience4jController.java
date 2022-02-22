package com.example.normalizer;

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
  private static final String STRING_FALLBACK_METHOD = "stringFallback";

  @GetMapping(
          value = "/response",
          produces = MediaType.APPLICATION_JSON_VALUE
  )
  @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = STRING_FALLBACK_METHOD)
  public Mono<Response<String>> response() {
    String test = "Test String";
    return Mono.just(test).flatMap(this::stringOkResponse);
  }

  public Mono<Response<String>> stringFallback(Exception ex) {
    return Mono.just(stringResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Fallback Executed"))
            .doOnNext(result -> log.warn("string fallback"));
  }

  private Mono<Response<String>> stringOkResponse (String string) {
    if (Math.random() > 0.5) {
      return Mono.error(new RuntimeException("error"));
    }
    return Mono.just(stringOkResponse());
  }

  private Response<String> stringOkResponse() { return stringResponse(HttpStatus.OK, "Test String"); }

  private Response<String> stringResponse(HttpStatus httpStatus, String result) {
    return Response.<String>builder()
            .code(httpStatus.value())
            .status(httpStatus.getReasonPhrase())
            .data(result)
            .build();
  }
}
