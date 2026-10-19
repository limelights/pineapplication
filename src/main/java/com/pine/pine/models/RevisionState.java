package com.pine.pine.models;

import jakarta.persistence.*;

@Entity
@Table(name = "revision_states", uniqueConstraints = @UniqueConstraint(
        name = "uk_revision_states_provider_connection",
        columnNames = {"provider", "connection_id"}))
public class RevisionState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String provider;

    @Column(name = "connection_id", nullable = false)
    private String connectionId;

    @Column(nullable = false)
    private Long revision;

    protected RevisionState() {}

    public RevisionState(final String provider, final String connectionId) {
        this.provider = provider;
        this.connectionId = connectionId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Long getLatestRevision() {
        return this.revision;
    }
}
