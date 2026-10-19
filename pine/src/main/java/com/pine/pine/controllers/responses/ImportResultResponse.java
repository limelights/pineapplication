package com.pine.pine.controllers.responses;

import com.pine.pine.models.ImportStatus.Outcome;

import java.time.Instant;
import java.util.List;

public record ImportResultResponse(String runId, String provider, String connectionId, Long revision,
                                   Outcome status, Instant ranAt, List<String> failures) {}
