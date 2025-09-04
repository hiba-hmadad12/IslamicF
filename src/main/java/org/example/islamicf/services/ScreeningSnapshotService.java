package org.example.islamicf.services;

import org.example.islamicf.entities.ScreeningSnapshot;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ScreeningSnapshotService {
    ScreeningSnapshot saveSnapshot(ScreeningSnapshot snapshot);

    ScreeningSnapshot getSnapshotById(Long id);
    void deleteSnapshotById(Long id);
    ScreeningSnapshot updateSnapshot(ScreeningSnapshot snapshot);

    List<ScreeningSnapshot> getAllSnapshots();

    // utiles pour ta page "API sélectionnée"
    List<ScreeningSnapshot> getSnapshotsByCompanyId(Long companyId);
    List<ScreeningSnapshot> getSnapshotsByCompanyIdAndSource(Long companyId, String source);
    ScreeningSnapshot getLatestSnapshotByCompanyId(Long companyId);
    ScreeningSnapshot getLatestSnapshotByCompanyIdAndSource(Long companyId, String source);
}
