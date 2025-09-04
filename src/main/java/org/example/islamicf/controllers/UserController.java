package org.example.islamicf.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.islamicf.entities.User;
import org.example.islamicf.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Angular dev
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    // -------------------------
    // AUTH
    // -------------------------

    /**
     * Inscription publique (force rôle USER par sécurité).
     * POST /api/auth/register
     */
    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest req) {
        User existing = userService.getUserByEmail(req.getEmail());
        if (existing != null) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        User u = new User();
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setFirstName(req.getFirstName());
        u.setLastName(req.getLastName());
        u.setRole("USER");            // sécurité: pas de self-assign ADMIN
        u.setEnabled(true);

        User saved = userService.saveUser(u);
        return ResponseEntity
                .created(URI.create("/api/users/" + saved.getId()))
                .body(UserDto.from(saved));
    }

    /**
     * Infos du user connecté (Basic Auth ou plus tard JWT).
     * GET /api/auth/me
     */
    @GetMapping("/auth/me")
    public ResponseEntity<UserDto> me(Principal principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userService.getUserByEmail(principal.getName());
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(UserDto.from(user));
    }

    // -------------------------
    // CRUD (admin/backoffice ou usage interne)
    // -------------------------

    /** GET /api/users */
    @GetMapping("/users")
    public List<UserDto> all() {
        return userService.getAllUsers().stream().map(UserDto::from).toList();
    }

    /** GET /api/users/{id} */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        User u = userService.getUserById(id);
        return (u == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(UserDto.from(u));
    }

    /** PUT /api/users/{id}  (email/nom/prénom/role/enabled/password) */
    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        User existing = userService.getUserById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        // email (vérifie l'unicité si changé)
        if (req.getEmail() != null && !req.getEmail().isBlank() && !Objects.equals(req.getEmail(), existing.getEmail())) {
            User byEmail = userService.getUserByEmail(req.getEmail());
            if (byEmail != null && !Objects.equals(byEmail.getId(), id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
            existing.setEmail(req.getEmail());
        }

        if (req.getFirstName() != null) existing.setFirstName(req.getFirstName());
        if (req.getLastName() != null)  existing.setLastName(req.getLastName());

        // rôle (si tu veux restreindre, fais-le au niveau Security)
        if (req.getRole() != null) {
            String role = req.getRole().toUpperCase();
            if (!role.equals("USER") && !role.equals("ADMIN")) {
                return ResponseEntity.badRequest().build();
            }
            existing.setRole(role);
        }

        if (req.getEnabled() != null) existing.setEnabled(req.getEnabled());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        User saved = userService.updateUser(existing);
        return ResponseEntity.ok(UserDto.from(saved));
    }

    /** DELETE /api/users/{id} */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User existing = userService.getUserById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------
    // DTOs
    // -------------------------

    @Data
    public static class RegisterRequest {
        @Email @NotBlank
        private String email;
        @NotBlank
        private String password;
        private String firstName;
        private String lastName;
    }

    @Data
    public static class UpdateUserRequest {
        @Email
        private String email;      // optionnel
        private String password;   // optionnel (sera BCrypt s'il est fourni)
        private String firstName;
        private String lastName;
        private String role;       // "USER" / "ADMIN"
        private Boolean enabled;   // true/false
    }

    @Data
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String email;
        private String role;
        private String firstName;
        private String lastName;
        private boolean enabled;

        public static UserDto from(User u) {
            return new UserDto(
                    u.getId(),
                    u.getEmail(),
                    u.getRole(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.isEnabled()
            );
        }
    }
}
