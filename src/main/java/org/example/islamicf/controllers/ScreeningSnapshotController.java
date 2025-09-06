package org.example.islamicf.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.islamicf.dto.ScreeningSnapshotDTO;
import org.example.islamicf.entities.Company;
import org.example.islamicf.entities.ScreeningSnapshot;
import org.example.islamicf.repositories.CompanyRepository;
import org.example.islamicf.services.ScreeningSnapshotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies/{companyId}/snapshots")
@CrossOrigin(origins = "http://localhost:4200")
public class ScreeningSnapshotController {

    private final ScreeningSnapshotService snapshotService;
    private final CompanyRepository companyRepository;

    @GetMapping
    public List<ScreeningSnapshotDTO> history(@PathVariable Long companyId) {
        return snapshotService.getHistory(companyId);
    }

    @GetMapping("/by-source/{source}")
    public List<ScreeningSnapshotDTO> historyBySource(@PathVariable Long companyId, @PathVariable String source) {
        return snapshotService.getHistoryBySource(companyId, source);
    }

    @GetMapping("/latest")
    public ResponseEntity<ScreeningSnapshotDTO> latest(
            @PathVariable Long companyId,
            @RequestParam(required = false) String source
    ) {
        ScreeningSnapshotDTO dto = (source == null || source.isBlank())
                ? snapshotService.getLatest(companyId)
                : snapshotService.getLatestBySource(companyId, source);
        return (dto == null) ? ResponseEntity.noContent().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScreeningSnapshotDTO> create(
            @PathVariable Long companyId,
            @Valid @RequestBody CreateSnapshotRequest body
    ) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + companyId));

        ScreeningSnapshot entity = ScreeningSnapshot.builder()
                .company(company)
                .source(body.source())
                .status(body.status() == null ? "UNKNOWN" : body.status())
                .haramRevenuePct(body.haramRevenuePct())
                .interestDebtPct(body.interestDebtPct())
                .interestIncomePct(body.interestIncomePct())
                .cashAndInterestPct(body.cashAndInterestPct())
                .fetchedAt(body.fetchedAt() == null ? Instant.now() : body.fetchedAt())
                .notes(body.notes())
                .build();

        return ResponseEntity.ok(snapshotService.saveSnapshot(entity));
    }

    public record CreateSnapshotRequest(
            String source,
            String status,
            BigDecimal haramRevenuePct,
            BigDecimal interestDebtPct,
            BigDecimal interestIncomePct,
            BigDecimal cashAndInterestPct,
            Instant fetchedAt,
            String notes
    ) {}
}
