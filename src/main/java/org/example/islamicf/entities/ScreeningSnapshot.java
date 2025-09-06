package org.example.islamicf.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "screening_snapshots", indexes = {
        @Index(name = "idx_snap_company_source_fetched", columnList = "company_id, source, fetched_at")
})
public class ScreeningSnapshot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false, length = 40)
    private String source;         // "ZOYA", "RAJHI", ...

    @Column(name="status", nullable = false, length = 20)
    private String status;         // "HALAL", "HARAM", "MIXED", "UNKNOWN"

    // ratios simplifiés (on reste sur 4 champs pour démarrer)
    @Column(name="haram_revenue_pct", precision = 10, scale = 4)
    private BigDecimal haramRevenuePct;

    @Column(name="interest_debt_pct", precision = 10, scale = 4)
    private BigDecimal interestDebtPct;

    @Column(name="interest_income_pct", precision = 10, scale = 4)
    private BigDecimal interestIncomePct;

    @Column(name="cash_interest_pct", precision = 10, scale = 4)
    private BigDecimal cashAndInterestPct;

    @Column(name="fetched_at", nullable = false)
    private Instant fetchedAt;

    @Column(length = 1000)
    private String notes;


    @Column(precision = 9, scale = 6)
    private BigDecimal purificationRatio;// raison/explication courte


    // ScreeningSnapshot.java
    @Column(precision = 9, scale = 6)
    private BigDecimal compliantRevenuePct;


    @PrePersist
    void prePersist() {
        if (fetchedAt == null) fetchedAt = Instant.now();
        if (status == null) status = "UNKNOWN";
    }
}
