package com.pine.pine.models.snapshots.paperledger;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class PaperLedgerCodeDeserializer extends ValueDeserializer<String> {
    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        final JsonNode value = ctxt.readTree(p);
        if (value != null) {
            if (value.isString()) {
                return value.asString();
            }
            return value.get(0).asString();
        }
        return ctxt.reportInputMismatch(String.class, "Expected string or array");
    }
}
