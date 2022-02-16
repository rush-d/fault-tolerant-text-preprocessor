package com.example.tokenizer;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@RestController
public class DataController {

    private TimeLimiter ourTimeLimiter = TimeLimiter.of(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(20)).build());

    public List<String> stringTokenizer(String string) {
        ArrayList tokens = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, ".:-! ");
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    @PostMapping(value = "/tokenizer", consumes = "application/json")
    public Callable<List<String>> getTokeinzerOutput(@RequestBody String string) {
        return TimeLimiter.decorateFutureSupplier(ourTimeLimiter, () ->
                CompletableFuture.supplyAsync(() -> {
                    List<String> tokens = new ArrayList<String>();
                    double random = Math.random();
                    double threshold = 0.7;
                    if (random < threshold) {
                        try {
                            tokens = stringTokenizer(string);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return tokens;
                    } else {
                        return new ArrayList<String>();
                    }
                }));
    }
}
