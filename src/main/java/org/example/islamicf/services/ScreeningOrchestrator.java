package org.example.islamicf.services;

import lombok.RequiredArgsConstructor;
import org.example.islamicf.dto.ScreeningSnapshotDTO;
import org.example.islamicf.entities.Company;
import org.example.islamicf.entities.ScreeningSnapshot;
import org.example.islamicf.mappers.ScreeningSnapshotMapper;
import org.example.islamicf.providers.ScreeningClient;
import org.example.islamicf.repositories.CompanyRepository;
import org.example.islamicf.repositories.ScreeningSnapshotRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreeningOrchestrator {

    private final CompanyRepository companies;
    private final ScreeningSnapshotRepository snapshots;
    private final List<ScreeningClient> clients; // ZoyaClient, RajhiClient, ...

    /** Sélectionne le provider par son code (ex: "ZOYA", "RAJHI") */
    private ScreeningClient bySource(String source) {
        return clients.stream()
                .filter(c -> c.source().equalsIgnoreCase(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown source: " + source));
    }

    /** Liste des providers disponibles */
    public List<String> providers() {
        return clients.stream().map(ScreeningClient::source).toList();
    }

    /** Dernier snapshot d’un provider donné pour une company */
    public ScreeningSnapshotDTO getLatest(Long companyId, String source) {
        return snapshots
                .findFirstByCompany_IdAndSourceOrderByFetchedAtDesc(companyId, source)
                .map(ScreeningSnapshotMapper::toDTO)
                .orElse(null);
    }

    /** Force un refresh depuis le provider (ADMIN seulement) */
    @PreAuthorize("hasRole('ADMIN')")
    public ScreeningSnapshotDTO refresh(Long companyId, String source, String apiKeyOverride) {
        Company company = companies.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + companyId));

        ScreeningClient client = bySource(source);

        // Appel provider -> DTO normalisé
        ScreeningSnapshotDTO dto = client.fetchAndNormalize(company, apiKeyOverride);

        // Sauvegarde en base
        ScreeningSnapshot entity = ScreeningSnapshotMapper.fromDTO(dto, company);
        ScreeningSnapshot saved = snapshots.save(entity);

        // On renvoie ce qui est réellement persisté
        return ScreeningSnapshotMapper.toDTO(saved);
    }
}
