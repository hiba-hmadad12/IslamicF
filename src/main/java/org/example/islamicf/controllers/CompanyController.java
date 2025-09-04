package org.example.islamicf.controllers;

import lombok.RequiredArgsConstructor;
import org.example.islamicf.entities.Company;
import org.example.islamicf.entities.ScreeningSnapshot;
import org.example.islamicf.services.CompanyService;
import org.example.islamicf.services.ScreeningSnapshotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    private final CompanyService companyService;
    private final ScreeningSnapshotService snapshotService;

    // GET /api/companies
    @GetMapping
    public List<Company> getAll() {
        return companyService.getAllCompanies();
    }

    // GET /api/companies/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Company> getById(@PathVariable Long id) {
        Company c = companyService.getCompanyById(id);
        return c == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(c);
    }

    // POST /api/companies
    @PostMapping
    public ResponseEntity<Company> create(@RequestBody Company company) {
        Company saved = companyService.saveCompany(company);
        return ResponseEntity.created(URI.create("/api/companies/" + saved.getId())).body(saved);
    }

    // PUT /api/companies/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Company> update(@PathVariable Long id, @RequestBody Company company) {
        Company existing = companyService.getCompanyById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        company.setId(id);
        Company updated = companyService.updateCompany(company);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/companies/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Company existing = companyService.getCompanyById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        companyService.deleteCompanyById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints utiles pour l’API sélectionnée sur ta page Angular ---

    // GET /api/companies/{id}/snapshots?source=ZOYA  (liste / historique par source)
    @GetMapping("/{id}/snapshots")
    public ResponseEntity<List<ScreeningSnapshot>> snapshotsBySource(
            @PathVariable Long id,
            @RequestParam(required = false) String source // null = toutes sources
    ) {
        Company company = companyService.getCompanyById(id);
        if (company == null) return ResponseEntity.notFound().build();

        List<ScreeningSnapshot> list = (source == null || source.isBlank())
                ? snapshotService.getSnapshotsByCompanyId(id)
                : snapshotService.getSnapshotsByCompanyIdAndSource(id, source);
        return ResponseEntity.ok(list);
    }

    // GET /api/companies/{id}/snapshots/latest?source=ZOYA  (dernier snapshot pour une source précise ou global)
    @GetMapping("/{id}/snapshots/latest")
    public ResponseEntity<ScreeningSnapshot> latestSnapshot(
            @PathVariable Long id,
            @RequestParam(required = false) String source
    ) {
        Company company = companyService.getCompanyById(id);
        if (company == null) return ResponseEntity.notFound().build();

        ScreeningSnapshot snap = (source == null || source.isBlank())
                ? snapshotService.getLatestSnapshotByCompanyId(id)
                : snapshotService.getLatestSnapshotByCompanyIdAndSource(id, source);

        return snap == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(snap);
    }
}
