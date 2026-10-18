package com.pine.pine.controllers;

import com.pine.pine.models.snapshots.EmberSnapshot;
import com.pine.pine.models.snapshots.NorthstarSnapshot;
import com.pine.pine.models.snapshots.paperledger.PaperLedgerSnapshot;
import com.pine.pine.parser.ParseResult;
import com.pine.pine.parser.Parser;
import com.pine.pine.repositories.CompanyRepository;
import com.pine.pine.services.CompanyService;
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
        if (body.containsKey("ledger")) {
            return importService.fromEmber(objectMapper.convertValue(body, EmberSnapshot.class));
        } else if (body.containsKey("provider")) {
            return importService.fromNorthStar(objectMapper.convertValue(body, NorthstarSnapshot.class));
        } else if (body.containsKey("source")) {
            return importService.fromPaperLedger(objectMapper.convertValue(body, PaperLedgerSnapshot.class));
        }
        return new ParseResult("unknown", ParseResult.INCOMPLETE);
    }
}
