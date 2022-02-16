package com.example.stopwordRemover;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@RestController
public class DataController {

    private final TimeLimiter ourTimeLimiter = TimeLimiter.of(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(20)).build());

    private final List<String> stopwords = new ArrayList<>(Arrays.asList("a", "an", "the", "is"));

    private List<String> remove(List<String> tokens) {
        List<String> filteredTokens = new ArrayList<String>();
        for (String token : tokens) {
            if (!stopwords.contains(token)) {
                filteredTokens.add(token);
            }
        }
        return filteredTokens;
    }

    @PostMapping(value = "/stopwordRemover", consumes = "application/json")
    public Callable<List<String>> removeStopwords(@RequestBody List<String> tokens) {
        return TimeLimiter.decorateFutureSupplier(ourTimeLimiter, () ->
                CompletableFuture.supplyAsync(() -> {
                    List<String> filteredTokens = new ArrayList<String>();
                    double random = Math.random();
                    double threshold = 0.7;
                    if (random < threshold) {
                        try {
                            filteredTokens = remove(tokens);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return filteredTokens;
                    } else {
                        return new ArrayList<String>();
                    }
                }));
    }

}
