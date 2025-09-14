package org.example.islamicf.services;

import org.example.islamicf.dto.ScreeningSnapshotDTO;
import org.example.islamicf.entities.ScreeningSnapshot;

import java.util.List;

public interface ScreeningSnapshotService {
    List<ScreeningSnapshotDTO> getHistory(Long companyId);
    List<ScreeningSnapshotDTO> getHistoryBySource(Long companyId, String source);
    ScreeningSnapshotDTO getLatest(Long companyId);
    ScreeningSnapshotDTO getLatestBySource(Long companyId, String source);

    // 👇 c’est cette méthode qui manque dans ton Impl
    ScreeningSnapshotDTO saveSnapshot(ScreeningSnapshot snapshot);
}