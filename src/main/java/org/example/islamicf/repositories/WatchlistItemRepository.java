package org.example.islamicf.repositories;

import org.example.islamicf.entities.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WatchlistItemRepository extends JpaRepository <WatchlistItem, Long> {
    List<WatchlistItem> findByUserId(Long userId);

    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);

    void deleteByUserIdAndCompanyId(Long userId, Long companyId);
}
