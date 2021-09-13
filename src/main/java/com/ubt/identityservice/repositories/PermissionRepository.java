package com.ubt.identityservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubt.identityservice.entities.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Permission findByName(String name);
}
