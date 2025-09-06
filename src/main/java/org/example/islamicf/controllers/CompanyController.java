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

    @GetMapping
    public List<Company> all() { return companyService.getAllCompanies(); }

    @GetMapping("/{id}")
    public ResponseEntity<Company> one(@PathVariable Long id) {
        Company c = companyService.getCompanyById(id);
        return c == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(c);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> create(@RequestBody @Valid Company company) {
        Company saved = companyService.saveCompany(company);
        return ResponseEntity.created(URI.create("/api/companies/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Company> update(@PathVariable Long id, @RequestBody @Valid Company company) {
        if (companyService.getCompanyById(id) == null) return ResponseEntity.notFound().build();
        company.setId(id);
        return ResponseEntity.ok(companyService.updateCompany(company));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        companyService.deleteCompanyById(id);
        return ResponseEntity.noContent().build();
    }
}
