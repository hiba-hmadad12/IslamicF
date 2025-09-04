package org.example.islamicf.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.islamicf.entities.User;
import org.example.islamicf.repositories.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(Transactional.TxType.SUPPORTS) // ou @Transactional(readOnly = true) avec Spring @Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = users.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email: " + username));

        String role = (u.getRole() == null ? "USER" : u.getRole().toUpperCase());

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())     // ⚠️ doit être BCrypt
                .roles(role)                   // ajoute automatiquement ROLE_
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(false)          // ne pas le lier à enabled
                .disabled(!u.isEnabled())      // enabled -> true pour autoriser la connexion
                .build();
    }
}
