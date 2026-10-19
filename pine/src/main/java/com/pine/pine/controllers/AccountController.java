package com.pine.pine.controllers;

import com.pine.pine.models.Account;
import com.pine.pine.models.AccountId;
import com.pine.pine.services.AccountAuditLogService;
import com.pine.pine.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountAuditLogService accountAuditLogService;

    public AccountController(final AccountService accountService, final AccountAuditLogService accountAuditLogService) {
        this.accountService = accountService;
        this.accountAuditLogService = accountAuditLogService;
    }

    @GetMapping
    public String accounts() {
        return "Stub: browse accounts and their availability.";
    }

    @PostMapping("/{accountId}/description")
    public ResponseEntity<Void> updateDescription(@PathVariable String accountId, @RequestBody String description) {
        final AccountId accountId1 = AccountId.fromString(accountId);
        final Account account = accountService.getByAccountId(accountId1);
        account.setDescription(description);
        accountAuditLogService.produceAuditLog(account, "Updated description");
        return ResponseEntity.status(HttpStatus.SEE_OTHER).location(URI.create("/")).build();
    }
}
