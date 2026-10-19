package com.pine.pine.controllers;

import com.pine.pine.models.Account;
import com.pine.pine.services.AccountService;
import com.pine.pine.services.CompanyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;

@Controller
public class IndexController {

    private final CompanyService companyService;
    private final AccountService accountService;

    public IndexController(final CompanyService companyService, final AccountService accountService) {
        this.companyService = companyService;
        this.accountService = accountService;
    }

    @GetMapping(path = "/")
    public String index(Model model) {
        var companies = companyService.getAll();
        var accountsByCompany = new LinkedHashMap<String, List<Account>>();
        for (var company : companies) {
            accountsByCompany.put(company.getId(), accountService.getByCompanyId(company));
        }
        model.addAttribute("companies", companies);
        model.addAttribute("accountsByCompany", accountsByCompany);
        return "index";
    }
}
