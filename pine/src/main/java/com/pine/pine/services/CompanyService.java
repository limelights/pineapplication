package com.pine.pine.services;

import com.pine.pine.models.Company;
import com.pine.pine.repositories.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(final CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void persistCompany(final Company company) {
        companyRepository.save(company);
    }
}
