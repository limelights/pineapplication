package com.pine.pine.services;

import com.pine.pine.models.Company;
import com.pine.pine.repositories.CompanyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(final CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company persistCompany(final Company company) {
        return companyRepository.findByName(company.getName())
                .orElseGet(() -> companyRepository.save(company));
    }

    public List<Company> getAll() {
        return companyRepository.findAll(Sort.by("id"));
    }
}
