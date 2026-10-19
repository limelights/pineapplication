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

    @Column(name = "revision", nullable = false)
    private String revision;

    protected AccountId() {
    }

    public AccountId(final String provider, final String connectionId, final String externalId, final String revision) {
        this.provider = requireNonBlank(provider, "provider");
        this.connectionId = requireNonBlank(connectionId, "connectionId");
        this.externalId = requireNonBlank(externalId, "externalId");
        this.revision = requireNonBlank(revision, "revision");
    }

    public static AccountId fromString(final String accountId) {
        final String[] splits = accountId.split("_");
        return new AccountId(splits[0], splits[1], splits[2], splits[3]);
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

    public String getRevision() { return revision; }

    public String asSlug() {
        return String.format("%s_%s_%s_%s", this.provider, this.connectionId, this.externalId, this.revision);
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
                && Objects.equals(externalId, accountId.externalId)
                && Objects.equals(revision, accountId.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider, connectionId, externalId, revision);
    }

    private static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
