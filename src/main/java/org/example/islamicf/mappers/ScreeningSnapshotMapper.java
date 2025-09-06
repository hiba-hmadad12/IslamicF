package org.example.islamicf.mappers;

import org.example.islamicf.dto.ScreeningSnapshotDTO;
import org.example.islamicf.entities.Company;
import org.example.islamicf.entities.ScreeningSnapshot;

import java.time.Instant;

public final class ScreeningSnapshotMapper {
    private ScreeningSnapshotMapper(){}

    public static ScreeningSnapshotDTO toDTO(ScreeningSnapshot s) {
        return ScreeningSnapshotDTO.builder()
                .companyId(s.getCompany().getId())
                .source(s.getSource())
                .fetchedAt(s.getFetchedAt())
                .status(s.getStatus())
                .notes(s.getNotes())
                .haramRevenuePct(s.getHaramRevenuePct())
                .interestDebtPct(s.getInterestDebtPct())
                .interestIncomePct(s.getInterestIncomePct())
                .cashAndInterestPct(s.getCashAndInterestPct())

                .purificationRatio(s.getPurificationRatio())

                .compliantRevenuePct(s.getCompliantRevenuePct())
                .build();
    }

    public static ScreeningSnapshot fromDTO(ScreeningSnapshotDTO dto, Company c) {
        return ScreeningSnapshot.builder()
                .company(c)
                .source(dto.getSource())
                .status(dto.getStatus() == null ? "UNKNOWN" : dto.getStatus())
                .haramRevenuePct(dto.getHaramRevenuePct())
                .interestDebtPct(dto.getInterestDebtPct())
                .interestIncomePct(dto.getInterestIncomePct())
                .cashAndInterestPct(dto.getCashAndInterestPct())
                .purificationRatio(dto.getPurificationRatio())

                .compliantRevenuePct(dto.getCompliantRevenuePct())
                .fetchedAt(dto.getFetchedAt() == null ? Instant.now() : dto.getFetchedAt())
                .notes(dto.getNotes())
                .build();
    }
}
