package com.pine.pine.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers/{provider}/connections/{connectionId}/catalogue")
public class CatalogueController {

    @GetMapping
    public String catalogue() {
        return "Stub: browse accounts, dimensions, and dimension values.";
    }

    @GetMapping("/availability")
    public String availability() {
        return "Stub: view which catalogue entries are currently available to use.";
    }
}
