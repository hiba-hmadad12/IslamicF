package org.example.islamicf.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password; // ⚠️ à hasher avec BCrypt
    private String role;     // "ADMIN" ou "USER"
    private String firstName;
    private String lastName;
    private boolean enabled = true;

    // Relations
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<WatchlistItem> watchlist;

}
