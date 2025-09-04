package org.example.islamicf.repositories;

import org.example.islamicf.entities.ScreeningSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreeningSnapshotRepository extends JpaRepository<ScreeningSnapshot, Long> {
    List<ScreeningSnapshot> findByCompanyIdOrderByFetchedAtDesc(Long companyId);

    List<ScreeningSnapshot> findByCompanyIdAndSourceOrderByFetchedAtDesc(Long companyId, String source);

    Optional<ScreeningSnapshot> findFirstByCompanyIdOrderByFetchedAtDesc(Long companyId);

    Optional<ScreeningSnapshot> findFirstByCompanyIdAndSourceOrderByFetchedAtDesc(Long companyId, String source);
}
