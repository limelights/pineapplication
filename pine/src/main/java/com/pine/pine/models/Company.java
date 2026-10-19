package com.pine.pine.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    protected Company() {
    }

    public Company(String name) {
        this(UUID.randomUUID().toString(), name);
    }

    public Company(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
