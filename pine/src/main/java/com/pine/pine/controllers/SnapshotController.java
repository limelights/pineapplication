package com.pine.pine.controllers;

import com.pine.pine.models.Accounting;
import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.parser.ParseResult;
import com.pine.pine.services.ImportService;
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
    private final ImportService importService;

    public SnapshotController(ObjectMapper objectMapper, ImportService importService) {
        this.objectMapper = objectMapper;
        this.importService = importService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ParseResult importSnapshot(@RequestBody Map<String, Object> body) {
        Accounting model = null;
        if (body.containsKey("ledger")) {
            model = objectMapper.convertValue(body, EmberSnapshot.class);
        } else if (body.containsKey("provider")) {
            model = objectMapper.convertValue(body, NorthstarSnapshot.class);
        } else if (body.containsKey("source")) {
            model = objectMapper.convertValue(body, PaperLedgerSnapshot.class);
        }
        return importService.importSnapshot(model);
    }
}
