package org.example.islamicf.controllers;

import lombok.RequiredArgsConstructor;
import org.example.islamicf.entities.Company;
import org.example.islamicf.entities.WatchlistItem;
import org.example.islamicf.services.CompanyService;
import org.example.islamicf.services.WatchlistItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/watchlist")
@CrossOrigin(origins = "http://localhost:4200")
public class WatchlistController {

    private final WatchlistItemService watchlistService;
    private final CompanyService companyService;

    // GET /api/users/{userId}/watchlist  -> renvoie les Company (plus pratique côté front)
    @GetMapping
    public ResponseEntity<List<Company>> getWatchlist(@PathVariable Long userId) {
        List<WatchlistItem> items = watchlistService.getWatchlistByUserId(userId);
        List<Company> companies = items.stream().map(WatchlistItem::getCompany).toList();
        return ResponseEntity.ok(companies);
    }

    // POST /api/users/{userId}/watchlist/{companyId}
    @PostMapping("/{companyId}")
    public ResponseEntity<?> add(@PathVariable Long userId, @PathVariable Long companyId) {
        if (watchlistService.existsByUserIdAndCompanyId(userId, companyId)) {
            return ResponseEntity.noContent().build(); // déjà présent
        }
        Company c = companyService.getCompanyById(companyId);
        if (c == null) return ResponseEntity.badRequest().body("Company not found");

        WatchlistItem saved = watchlistService.saveWatchlistItem(
                WatchlistItem.builder().company(c).user(
                        // côté service tu récupères le user par son id ou tu utilises un User stub {id}
                        // si tu as l'entité User chargée ailleurs, remplace par le vrai user
                        // pour rester simple:
                        org.example.islamicf.entities.User.builder().id(userId).build()
                ).build()
        );
        return ResponseEntity.created(URI.create("/api/users/" + userId + "/watchlist/" + companyId)).body(saved);
    }

    // DELETE /api/users/{userId}/watchlist/{companyId}
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> remove(@PathVariable Long userId, @PathVariable Long companyId) {
        watchlistService.deleteByUserIdAndCompanyId(userId, companyId);
        return ResponseEntity.noContent().build();
    }
}
