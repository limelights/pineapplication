package com.pine.pine.controllers;

import com.pine.pine.models.Account;
import com.pine.pine.services.AccountAuditLogService;
import com.pine.pine.services.AccountService;
import com.pine.pine.services.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class IndexController {

    private final CompanyService companyService;
    private final AccountService accountService;
    private final AccountAuditLogService accountAuditLogService;

    public IndexController(final CompanyService companyService, final AccountService accountService,
                           final AccountAuditLogService accountAuditLogService) {
        this.companyService = companyService;
        this.accountService = accountService;
        this.accountAuditLogService = accountAuditLogService;
    }

    @GetMapping(path = "/")
    public String index(Model model) {
        var companies = companyService.getAll();
        var currentAccountsByCompany = new LinkedHashMap<String, List<Account>>();
        var olderAccountsByCompany = new LinkedHashMap<String, List<Account>>();
        for (var company : companies) {
            var currentAccounts = new ArrayList<Account>();
            var olderAccounts = new ArrayList<Account>();
            var seenAccounts = new HashSet<List<String>>();
            // Accounts arrive newest first, so the first version of each logical key is current.
            for (var account : accountService.getByCompanyId(company)) {
                var key = account.getKey();
                var identity = List.of(key.getProvider(), key.getConnectionId(), key.getExternalId());
                if (seenAccounts.add(identity)) {
                    currentAccounts.add(account);
                } else {
                    olderAccounts.add(account);
                }
            }
            currentAccountsByCompany.put(company.getId(), currentAccounts);
            olderAccountsByCompany.put(company.getId(), olderAccounts);
        }
        model.addAttribute("companies", companies);
        model.addAttribute("currentAccountsByCompany", currentAccountsByCompany);
        model.addAttribute("olderAccountsByCompany", olderAccountsByCompany);
        model.addAttribute("auditLogs", accountAuditLogService.getAll());
        return "index";
    }
}
