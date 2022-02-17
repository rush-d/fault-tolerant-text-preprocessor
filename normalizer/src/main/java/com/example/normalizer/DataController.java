package com.example.normalizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DataController {

    @Autowired
    private DataService dataService;

    @PostMapping(value = "/normalizer")
    public String normalizerOutput(@RequestBody String string) {
        return dataService.output(string);
    }
}
