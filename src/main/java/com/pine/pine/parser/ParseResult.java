package com.pine.pine.parser;

public class ParseResult {
    public static final String INCOMPLETE = "incomplete";
    public static final String COMPLETE = "complete";

    private String status = INCOMPLETE;
    private String type = "";

    public ParseResult(final String type, final String status) {
        this.type = type;
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public String getType() {
        return this.type;
    }
}
