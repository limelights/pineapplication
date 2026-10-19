package com.pine.pine.repositories;

import com.pine.pine.models.RevisionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RevisionStateRepository extends JpaRepository<RevisionState, Long> {
    Optional<RevisionState> findByProviderAndConnectionId(String providerId, String connectionId);
}
