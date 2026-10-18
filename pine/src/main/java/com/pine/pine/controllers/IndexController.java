package com.pine.pine.controllers;

import com.pine.pine.services.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final CompanyService companyService;

    public IndexController(final CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping(path = "/")
    public String main(Model model) {
        model.addAttribute("companies", companyService.getAll());
        return "index";
    }
}
