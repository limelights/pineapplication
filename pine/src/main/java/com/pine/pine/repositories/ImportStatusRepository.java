package com.pine.pine.repositories;

import com.pine.pine.models.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImportStatusRepository extends JpaRepository<ImportStatus, Long> {
    Optional<ImportStatus> findByCompanyNameAndProviderAndConnectionId(
            String companyName, String provider, String connectionId);

    List<ImportStatus> findByCompanyNameOrderByProviderAscConnectionIdAsc(String companyName);
}
