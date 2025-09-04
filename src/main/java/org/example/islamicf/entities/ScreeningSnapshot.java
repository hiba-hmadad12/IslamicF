package org.example.islamicf.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScreeningSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Company company;

    private String source; // "FMP", "ZOYA", "RAJHI"
    private String status; // "HALAL", "HARAM", "MIXED", "UNKNOWN"

    private Double haramRevenuePct;
    private Double interestDebtPct;
    private Double interestIncomePct;
    private Double cashAndInterestPct;

    private Instant fetchedAt;
    private String notes;
}