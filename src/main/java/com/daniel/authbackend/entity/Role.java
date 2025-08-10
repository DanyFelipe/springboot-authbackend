package com.daniel.authbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // Ej: ROLE_ADMIN, ROLE_USER

    @ManyToMany(mappedBy = "roles")
    private Set<User> users; // Relación bidireccional, opcional para este caso
}
