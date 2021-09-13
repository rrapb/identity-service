package com.ubt.identityservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ubt.identityservice.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
    List<Role> findAllByEnabled(boolean enabled);
}
