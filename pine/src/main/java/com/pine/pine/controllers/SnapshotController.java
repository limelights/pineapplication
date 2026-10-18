package com.pine.pine.controllers;

import com.pine.pine.models.EmberSnapshot;
import com.pine.pine.models.NorthstarSnapshot;
import com.pine.pine.models.PaperLedgerSnapshot;
import com.pine.pine.parser.ParseResult;
import com.pine.pine.parser.Parser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("/api/snapshots/{company-name}")
public class SnapshotController {

    private final ObjectMapper objectMapper;

    public SnapshotController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ParseResult importSnapshot(@RequestBody Map<String, Object> body) {
        if (body.containsKey("ledger")) {
            return Parser.fromEmber(objectMapper.convertValue(body, EmberSnapshot.class));
        } else if (body.containsKey("provider")) {
            return Parser.fromNorthStar(objectMapper.convertValue(body, NorthstarSnapshot.class));
        } else if (body.containsKey("source")) {
            return Parser.fromPaperLedger(objectMapper.convertValue(body, PaperLedgerSnapshot.class));
        }
        return new ParseResult("unknown", ParseResult.INCOMPLETE);
    }
}
