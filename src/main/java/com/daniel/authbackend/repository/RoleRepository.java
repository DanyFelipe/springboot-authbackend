package com.daniel.authbackend.repository;

import com.daniel.authbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByUsername(String username);
}
