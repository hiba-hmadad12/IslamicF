package org.example.islamicf.controllers;

import lombok.RequiredArgsConstructor;
import org.example.islamicf.entities.ScreeningSnapshot;
import org.example.islamicf.services.ScreeningSnapshotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/snapshots")
@CrossOrigin(origins = "http://localhost:4200")
public class ScreeningSnapshotController {

    private final ScreeningSnapshotService snapshotService;

    @GetMapping
    public List<ScreeningSnapshot> getAll() {
        return snapshotService.getAllSnapshots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScreeningSnapshot> getById(@PathVariable Long id) {
        ScreeningSnapshot s = snapshotService.getSnapshotById(id);
        return s == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(s);
    }

    @PostMapping
    public ResponseEntity<ScreeningSnapshot> create(@RequestBody ScreeningSnapshot snapshot) {
        ScreeningSnapshot saved = snapshotService.saveSnapshot(snapshot);
        return ResponseEntity.created(URI.create("/api/snapshots/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScreeningSnapshot> update(@PathVariable Long id, @RequestBody ScreeningSnapshot snapshot) {
        ScreeningSnapshot existing = snapshotService.getSnapshotById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        snapshot.setId(id);
        return ResponseEntity.ok(snapshotService.updateSnapshot(snapshot));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ScreeningSnapshot existing = snapshotService.getSnapshotById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        snapshotService.deleteSnapshotById(id);
        return ResponseEntity.noContent().build();
    }
}
