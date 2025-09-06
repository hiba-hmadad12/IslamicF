package org.example.islamicf.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data @Builder
public class ScreeningSnapshotDTO {
    private Long companyId;

    private String source;              // "ZOYA", "RAJHI", ...
    private Instant fetchedAt;          // quand le provider a été lu

    private String status;              // "HALAL", "HARAM", "MIXED", "UNKNOWN"
    private String notes;               // explication courte

    // 4 ratios normalisés
    private BigDecimal haramRevenuePct;
    private BigDecimal interestDebtPct;
    private BigDecimal interestIncomePct;
    private BigDecimal cashAndInterestPct;

    private BigDecimal purificationRatio;



    private BigDecimal compliantRevenuePct;
}
