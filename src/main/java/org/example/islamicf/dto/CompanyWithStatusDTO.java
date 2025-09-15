package org.example.islamicf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.islamicf.entities.Company;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyWithStatusDTO {
    private Long id;
    private String symbol;
    private String name;
    private String sector;
    private String country;
    private String status; // "HALAL", "HARAM", "MIXED", "UNKNOWN"

    // Static factory method to create from a Company and status
    public static CompanyWithStatusDTO from(Company company, String status) {
        return CompanyWithStatusDTO.builder()
                .id(company.getId())
                .symbol(company.getSymbol())
                .name(company.getName())
                .sector(company.getSector())
                .country(company.getCountry())
                .status(status)
                .build();
    }
}
