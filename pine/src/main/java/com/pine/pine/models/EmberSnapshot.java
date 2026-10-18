package com.pine.pine.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record EmberSnapshot(
        String installation,
        Long serial,
        @JsonProperty("captured_at") String capturedAt,
        Map<String, LedgerEntry> ledger,
        Map<String, TrackingDimension> tracking) {

    public record LedgerEntry(
            String number,
            String title,
            @JsonProperty("is_archived") Boolean isArchived) {
    }

    public record TrackingDimension(String label, Map<String, Member> members) {
    }

    public record Member(
            String value,
            String label,
            @JsonProperty("is_archived") Boolean isArchived) {
    }
}
