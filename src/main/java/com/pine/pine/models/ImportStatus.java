package com.pine.pine.models;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "import_statuses", uniqueConstraints = @UniqueConstraint(
        name = "uk_import_status_company_provider_connection",
        columnNames = {"company_name", "provider", "connection_id"}))
public class ImportStatus {
    public enum Outcome { SUCCESS, PARTIAL, FAILURE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String provider;

    @Column(name = "connection_id", nullable = false)
    private String connectionId;

    @Column(name = "run_id", nullable = false)
    private String runId;

    private Long revision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Outcome status;

    @Column(name = "ran_at", nullable = false)
    private Instant ranAt;

    protected ImportStatus() {}

    public ImportStatus(String companyName, String provider, String connectionId) {
        this.companyName = companyName;
        this.provider = provider;
        this.connectionId = connectionId;
    }

    public void complete(String runId, Long revision, Outcome status, Instant ranAt) {
        this.runId = runId;
        this.revision = revision;
        this.status = status;
        this.ranAt = ranAt;
    }

    public String getProvider() { return provider; }
    public String getConnectionId() { return connectionId; }
    public String getRunId() { return runId; }
    public Long getRevision() { return revision; }
    public Outcome getStatus() { return status; }
    public Instant getRanAt() { return ranAt; }
}
