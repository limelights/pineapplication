package com.pine.pine.models.paperledger;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class PaperLedgerDisplayNameDeserializer extends ValueDeserializer<PaperLedgerSnapshot.DisplayName> {
    @Override
    public PaperLedgerSnapshot.DisplayName deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        final JsonNode value = ctxt.readTree(p);
        if (value != null) {
            if (value.isString()) {
                return new PaperLedgerSnapshot.DisplayName(value.asString(), "unknown");
            }
            return new PaperLedgerSnapshot.DisplayName(
                    value.get("primary").asString(),
                    value.get("locale").asString());
        }
        return ctxt.reportInputMismatch(String.class, "Expected string or object");
    }
}
