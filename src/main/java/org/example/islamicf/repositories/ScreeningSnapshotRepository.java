package org.example.islamicf.repositories;

import org.example.islamicf.entities.ScreeningSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScreeningSnapshotRepository extends JpaRepository<ScreeningSnapshot, Long> {
    List<ScreeningSnapshot> findByCompany_IdOrderByFetchedAtDesc(Long companyId);
    List<ScreeningSnapshot> findByCompany_IdAndSourceOrderByFetchedAtDesc(Long companyId, String source);
    Optional<ScreeningSnapshot> findFirstByCompany_IdOrderByFetchedAtDesc(Long companyId);
    Optional<ScreeningSnapshot> findFirstByCompany_IdAndSourceOrderByFetchedAtDesc(Long companyId, String source);
}
