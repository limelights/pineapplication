package com.pine.pine.controllers;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {


    @GetMapping(path = "/")
    public String main(HttpMethod httpMethod) {
        return "hello world";
    }
}