package com.pine.pine.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AccountId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    private String provider;

    @Column(name = "connection_id", nullable = false)
    private String connectionId;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    protected AccountId() {
    }

    public AccountId(String provider, String connectionId, String externalId) {
        this.provider = requireNonBlank(provider, "provider");
        this.connectionId = requireNonBlank(connectionId, "connectionId");
        this.externalId = requireNonBlank(externalId, "externalId");
    }

    public String getProvider() {
        return provider;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getExternalId() {
        return externalId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AccountId accountId)) {
            return false;
        }
        return Objects.equals(provider, accountId.provider)
                && Objects.equals(connectionId, accountId.connectionId)
                && Objects.equals(externalId, accountId.externalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, connectionId, externalId);
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
