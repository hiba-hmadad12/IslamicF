package org.example.islamicf.providers;

import org.example.islamicf.dto.ScreeningSnapshotDTO;
import org.example.islamicf.entities.Company;

public interface ScreeningClient {
    String source();

    /** Ancienne signature : on la garde en default pour compatibilité */
    default ScreeningSnapshotDTO fetchAndNormalize(Company company) {
        return fetchAndNormalize(company, null);
    }

    /** Nouvelle signature avec override de clé (peut être null) */
    ScreeningSnapshotDTO fetchAndNormalize(Company company, String apiKeyOverride);
}
