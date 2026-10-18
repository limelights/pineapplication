package com.pine.pine.models;

public class Account {
    private Long uid;
    private String name;
    private String id;

    public Account(final String name, final String id) {
        this(null, name, id);
    }

    public Account(final Long uid, final String name, final String id) {
        this.uid = uid;
        this.name = name;
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(final Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
