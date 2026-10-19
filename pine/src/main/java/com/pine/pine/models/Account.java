package com.pine.pine.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account {
    @EmbeddedId
    private AccountId key;

    private String name;

    @Column(nullable = false)
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = true)
    private String description;

    protected Account() {
    }

    public Account(final String name, final AccountId key) {
        this.key = Objects.requireNonNull(key, "key");
        this.name = name;
    }

    public AccountId getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getId() {
        return key.getExternalId();
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getRevision() {
        return key.getRevision();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
