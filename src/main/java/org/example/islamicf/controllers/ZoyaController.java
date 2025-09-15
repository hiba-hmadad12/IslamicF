package org.example.islamicf.controllers;

import lombok.RequiredArgsConstructor;
import org.example.islamicf.dto.CompanyWithStatusDTO;
import org.example.islamicf.providers.ZoyaClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class ZoyaController {

    private static final Logger log = LoggerFactory.getLogger(ZoyaController.class);
    private final ZoyaClient zoyaClient;

    @PostMapping("/zoya/companies")
    public ResponseEntity<?> getAllCompanies(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String nextToken,
            @RequestHeader(name = "x-api-key", required = false) String apiKeyOverride
    ) {
        log.info("Fetching all companies with limit={}, nextToken={}", limit, nextToken);

        // Call Zoya API to get all companies data and map to CompanyWithStatusDTO objects
        List<CompanyWithStatusDTO> companies = zoyaClient.fetchAndMapCompaniesWithStatus(limit, nextToken, apiKeyOverride);

        if (companies == null) {
            log.error("Failed to fetch all companies data from Zoya API");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Failed to fetch all companies data from Zoya API"));
        }

        log.info("Successfully fetched and mapped {} companies with status", companies.size());
        return ResponseEntity.ok(companies);
    }
}
