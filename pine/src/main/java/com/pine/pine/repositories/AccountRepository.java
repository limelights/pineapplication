package com.pine.pine.repositories;

import com.pine.pine.models.Account;
import com.pine.pine.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCompanyOrderByUidAsc(Company company);
}
