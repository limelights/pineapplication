package com.pine.pine.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/providers/{provider}/connections/{connectionId}/catalogue/accounts")
public class AccountController {

    @GetMapping
    public String accounts() {
        return "Stub: browse accounts and their availability.";
    }

    @PutMapping("/{accountId}/description")
    public String updateDescription() {
        return "Stub: add or edit the local description of an account.";
    }
}
