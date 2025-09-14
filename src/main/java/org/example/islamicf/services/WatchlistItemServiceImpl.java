package org.example.islamicf.services;

import lombok.AllArgsConstructor;
import org.example.islamicf.entities.WatchlistItem;
import org.example.islamicf.repositories.WatchlistItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WatchlistItemServiceImpl implements WatchlistItemService {

    private final WatchlistItemRepository watchlistItemRepository;

    @Override
    public WatchlistItem saveWatchlistItem(WatchlistItem item) {
        return watchlistItemRepository.save(item);
    }

    @Override
    public WatchlistItem getWatchlistItemById(Long id) {
        return watchlistItemRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteWatchlistItemById(Long id) {
        watchlistItemRepository.deleteById(id);
    }

    @Override
    public WatchlistItem updateWatchlistItem(WatchlistItem item) {
        return watchlistItemRepository.save(item);
    }

    @Override
    public List<WatchlistItem> getWatchlistByUserId(Long userId) {
        return watchlistItemRepository.findByUserId(userId);
    }

    @Override
    public boolean existsByUserIdAndCompanyId(Long userId, Long companyId) {
        return watchlistItemRepository.existsByUserIdAndCompanyId(userId, companyId);
    }

    @Override
    public void deleteByUserIdAndCompanyId(Long userId, Long companyId) {
        watchlistItemRepository.deleteByUserIdAndCompanyId(userId, companyId);
    }
}