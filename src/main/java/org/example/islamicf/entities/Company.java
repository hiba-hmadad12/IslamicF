package org.example.islamicf.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol; // e.g. AAPL
    private String name;
    private String sector;
    private String country;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private List<ScreeningSnapshot> snapshots;
}
