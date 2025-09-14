package org.example.islamicf.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.islamicf.entities.Company;
import org.example.islamicf.services.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:4200")
public class CompanyController {

    private final CompanyService companyService;

    // Récupérer toutes les entreprises locales
    @GetMapping
    public List<Company> all() {
        return companyService.getAllCompanies();
    }

    // Récupérer une entreprise par ID
    @GetMapping("/{id}")
    public ResponseEntity<Company> one(@PathVariable("id") Long id) {
        Company c = companyService.getCompanyById(id);
        return c == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(c);
    }

    // Créer une entreprise (ADMIN uniquement)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> create(@RequestBody @Valid Company company) {
        Company saved = companyService.saveCompany(company);
        return ResponseEntity.created(URI.create("/api/companies/" + saved.getId())).body(saved);
    }

    // Mettre à jour une entreprise (ADMIN uniquement)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> update(@PathVariable("id") Long id,
                                          @RequestBody @Valid Company company) {
        if (companyService.getCompanyById(id) == null) return ResponseEntity.notFound().build();
        company.setId(id);
        return ResponseEntity.ok(companyService.updateCompany(company));
    }

    // Supprimer une entreprise (ADMIN uniquement)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        companyService.deleteCompanyById(id);
        return ResponseEntity.noContent().build();
    }
}
