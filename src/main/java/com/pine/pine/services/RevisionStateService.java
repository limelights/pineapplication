package com.pine.pine.services;

import com.pine.pine.models.RevisionState;
import com.pine.pine.repositories.RevisionStateRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RevisionStateService {

    private final RevisionStateRepository revisionStateRepository;

    public RevisionStateService(RevisionStateRepository revisionStateRepository) {
        this.revisionStateRepository = revisionStateRepository;
    }

    public Optional<RevisionState> findByProviderAndConnectionId(final String provider, final String connectionId) {
        return revisionStateRepository.findByProviderAndConnectionId(provider, connectionId);
    }

    public void saveRevisionState(final RevisionState revisionState) {
        revisionStateRepository.save(revisionState);
    }
}
