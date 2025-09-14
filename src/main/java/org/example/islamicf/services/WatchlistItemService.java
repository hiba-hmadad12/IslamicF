package org.example.islamicf.services;

import org.example.islamicf.entities.WatchlistItem;
import org.springframework.stereotype.Service;

import java.util.List;


public interface WatchlistItemService {
    WatchlistItem saveWatchlistItem(WatchlistItem item);

    WatchlistItem getWatchlistItemById(Long id);
    void deleteWatchlistItemById(Long id);
    WatchlistItem updateWatchlistItem(WatchlistItem item);

    List<WatchlistItem> getWatchlistByUserId(Long userId);

    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);
    void deleteByUserIdAndCompanyId(Long userId, Long companyId);
}