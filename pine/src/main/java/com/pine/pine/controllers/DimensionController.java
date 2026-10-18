package com.pine.pine.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers/{provider}/connections/{connectionId}/catalogue/dimensions")
public class DimensionController {

    @GetMapping
    public String dimensions() {
        return "Stub: browse dimensions and their availability.";
    }

    @GetMapping("/{dimensionId}/values")
    public String dimensionValues() {
        return "Stub: browse the values of a dimension and their availability.";
    }

    @PutMapping("/{dimensionId}/values/{valueId}/description")
    public String updateValueDescription() {
        return "Stub: add or edit the local description of a dimension value.";
    }
}
