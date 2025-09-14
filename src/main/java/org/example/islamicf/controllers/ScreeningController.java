// src/main/java/org/example/islamicf/controllers/ScreeningController.java
package org.example.islamicf.controllers;

import lombok.RequiredArgsConstructor;
import org.example.islamicf.dto.ScreeningSnapshotDTO;
import org.example.islamicf.services.ScreeningOrchestrator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
public class ScreeningController {

    private final ScreeningOrchestrator orchestrator;

    // GET latest snapshot for a source
    @GetMapping("/{id}/screen")
    public ScreeningSnapshotDTO getLatest(
            @PathVariable Long id,
            @RequestParam String source
    ) {
        return orchestrator.getLatest(id, source);
    }

    // POST to refresh from a provider (ADMIN)
    @PostMapping("/{id}/screen/refresh")
    public ScreeningSnapshotDTO refresh(
            @PathVariable Long id,
            @RequestParam String source,
            @RequestHeader(name = "x-api-key", required = false) String apiKeyOverride
    ) {
        return orchestrator.refresh(id, source, apiKeyOverride);
    }
}