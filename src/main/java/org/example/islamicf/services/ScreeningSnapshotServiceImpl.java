package org.example.islamicf.services;

import lombok.RequiredArgsConstructor;
import org.example.islamicf.dto.ScreeningSnapshotDTO;
import org.example.islamicf.entities.ScreeningSnapshot;
import org.example.islamicf.mappers.ScreeningSnapshotMapper;
import org.example.islamicf.repositories.ScreeningSnapshotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreeningSnapshotServiceImpl implements ScreeningSnapshotService {

    // ⚠️ le nom du champ doit exister et être identique ici et dans le code
    private final ScreeningSnapshotRepository snapshotRepository;

    @Override
    public List<ScreeningSnapshotDTO> getHistory(Long companyId) {
        return snapshotRepository.findByCompany_IdOrderByFetchedAtDesc(companyId)
                .stream().map(ScreeningSnapshotMapper::toDTO).toList();
    }

    @Override
    public List<ScreeningSnapshotDTO> getHistoryBySource(Long companyId, String source) {
        return snapshotRepository.findByCompany_IdAndSourceOrderByFetchedAtDesc(companyId, source)
                .stream().map(ScreeningSnapshotMapper::toDTO).toList();
    }

    @Override
    public ScreeningSnapshotDTO getLatest(Long companyId) {
        return snapshotRepository.findFirstByCompany_IdOrderByFetchedAtDesc(companyId)
                .map(ScreeningSnapshotMapper::toDTO)
                .orElse(null);
    }

    @Override
    public ScreeningSnapshotDTO getLatestBySource(Long companyId, String source) {
        return snapshotRepository.findFirstByCompany_IdAndSourceOrderByFetchedAtDesc(companyId, source)
                .map(ScreeningSnapshotMapper::toDTO)
                .orElse(null);
    }

    @Override
    public ScreeningSnapshotDTO saveSnapshot(ScreeningSnapshot snapshot) {
        ScreeningSnapshot saved = snapshotRepository.save(snapshot);
        return ScreeningSnapshotMapper.toDTO(saved);
    }
}