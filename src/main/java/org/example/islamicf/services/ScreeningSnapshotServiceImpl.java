package org.example.islamicf.services;

import lombok.AllArgsConstructor;
import org.example.islamicf.entities.ScreeningSnapshot;
import org.example.islamicf.repositories.ScreeningSnapshotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ScreeningSnapshotServiceImpl implements ScreeningSnapshotService {

    private final ScreeningSnapshotRepository snapshotRepository;

    @Override
    public ScreeningSnapshot saveSnapshot(ScreeningSnapshot snapshot) {
        return snapshotRepository.save(snapshot);
    }

    @Override
    public ScreeningSnapshot getSnapshotById(Long id) {
        return snapshotRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteSnapshotById(Long id) {
        snapshotRepository.deleteById(id);
    }

    @Override
    public ScreeningSnapshot updateSnapshot(ScreeningSnapshot snapshot) {
        return snapshotRepository.save(snapshot);
    }

    @Override
    public List<ScreeningSnapshot> getAllSnapshots() {
        return snapshotRepository.findAll();
    }

    @Override
    public List<ScreeningSnapshot> getSnapshotsByCompanyId(Long companyId) {
        return snapshotRepository.findByCompanyIdOrderByFetchedAtDesc(companyId);
    }

    @Override
    public List<ScreeningSnapshot> getSnapshotsByCompanyIdAndSource(Long companyId, String source) {
        return snapshotRepository.findByCompanyIdAndSourceOrderByFetchedAtDesc(companyId, source);
    }

    @Override
    public ScreeningSnapshot getLatestSnapshotByCompanyId(Long companyId) {
        return snapshotRepository.findFirstByCompanyIdOrderByFetchedAtDesc(companyId).orElse(null);
    }

    @Override
    public ScreeningSnapshot getLatestSnapshotByCompanyIdAndSource(Long companyId, String source) {
        return snapshotRepository.findFirstByCompanyIdAndSourceOrderByFetchedAtDesc(companyId, source).orElse(null);
    }
}
