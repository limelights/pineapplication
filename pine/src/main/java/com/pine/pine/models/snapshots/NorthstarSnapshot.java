package com.pine.pine.models.snapshots;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pine.pine.models.Account;
import com.pine.pine.models.AccountId;
import com.pine.pine.models.Accounting;
import com.pine.pine.models.Company;

import java.util.List;

public class NorthstarSnapshot implements Accounting {
    private String provider;
    private String connectionId;
    private Long generation;
    private String generatedAt;
    @JsonProperty("accounts")
    private List<NorthStarAccount> providerAccounts;
    private List<Dimension> dimensions;

    public NorthstarSnapshot() {
    }

    public NorthstarSnapshot(String provider, String connectionId, Long generation, String generatedAt,
                             List<NorthStarAccount> accounts, List<Dimension> dimensions) {
        this.provider = provider;
        this.connectionId = connectionId;
        this.generation = generation;
        this.generatedAt = generatedAt;
        this.providerAccounts = accounts;
        this.dimensions = dimensions;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public Long getGeneration() {
        return generation;
    }

    public void setGeneration(Long generation) {
        this.generation = generation;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public List<NorthStarAccount> getProviderAccounts() {
        return providerAccounts;
    }

    public void setProviderAccounts(List<NorthStarAccount> providerAccounts) {
        this.providerAccounts = providerAccounts;
    }

    public List<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

    public record NorthStarAccount(String id, String code, String name, Boolean enabled) {
    }

    public record Dimension(String id, String name, List<DimensionValue> values) {
    }

    public record DimensionValue(String id, String code, String name, Boolean enabled) {
    }

    @Override
    @JsonIgnore
    public List<com.pine.pine.models.Account> getAccounts() {
        if (providerAccounts == null) {
            return List.of();
        }
        return providerAccounts.stream()
                .map(nsaccount -> {
                    var account = new Account(nsaccount.name(), new AccountId("northstar", connectionId, nsaccount.id()));
                    account.setStatus(nsaccount.enabled());
                    return account;
                })
                .toList();
    }

    @Override
    public Company getCompany() {
        return new Company(this.getConnectionId());
    }

}
