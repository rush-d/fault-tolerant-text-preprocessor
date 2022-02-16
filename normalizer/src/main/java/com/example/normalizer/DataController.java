package com.example.normalizer;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@RestController
public class DataController {

    private String text = "";

    private TimeLimiter ourTimeLimiter = TimeLimiter.of(TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofMillis(20)).build());

    private static String readTextFile(String fileName) throws Exception {
        return new String(Files.readAllBytes(Paths.get(fileName))).toLowerCase();
    }

    @PostMapping(value = "/normalizer", consumes = "application/json")
    public Callable<String> getNormalizerOutput(@RequestBody String string) {
        return TimeLimiter.decorateFutureSupplier(ourTimeLimiter, () ->
                CompletableFuture.supplyAsync(() -> {
                    String fileName = "data.txt";
                    String filePath = System.getProperty("user.dir") + "\\src\\main\\java\\com\\example\\normalizer\\" + fileName;
                    String outputString = "";
                    double random = Math.random();
                    double threshold = 0.7;
                    if (random < threshold) {
                        try {
                            outputString = string.toLowerCase();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return outputString;
                    } else {
                        return "Normalizer Service is DOWN";
                    }
                }));
    }
}
