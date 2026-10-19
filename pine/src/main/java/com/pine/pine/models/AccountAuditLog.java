package com.pine.pine.models;

import jakarta.persistence.*;

@Entity
@Table(name = "account_audit_log")
public class AccountAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "account_provider", referencedColumnName = "provider"),
            @JoinColumn(name = "account_connection_id", referencedColumnName = "connection_id"),
            @JoinColumn(name = "account_external_id", referencedColumnName = "external_id"),
            @JoinColumn(name = "account_revision", referencedColumnName = "revision")
    })
    private Account account;

    @Column(nullable = false)
    private String reason;

    public AccountAuditLog(final Account account, final String reason) {
        this.account = account;
        this.reason = reason;
    }

    public AccountAuditLog() {}

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
