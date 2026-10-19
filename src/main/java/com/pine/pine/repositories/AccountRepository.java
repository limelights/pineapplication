package com.pine.pine.repositories;

import com.pine.pine.models.Account;
import com.pine.pine.models.AccountId;
import com.pine.pine.models.Company;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, AccountId> {
    List<Account> findByCompany(Company company, Sort sort);

    List<Account> findByKeyProviderAndKeyConnectionId(String provider, String connectionId);

    Account getByKey(AccountId accountId);

    Optional<Account> findFirstByKeyProviderAndKeyConnectionIdAndKeyExternalIdOrderByKeyRevisionDesc(
            String provider,
            String connectionId,
            String externalId);
}
