package com.pine.pine.models;

import java.util.UUID;

public class Company {
    private final String id;
    private String name;

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
