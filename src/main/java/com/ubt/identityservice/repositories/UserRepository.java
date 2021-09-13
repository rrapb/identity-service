package com.ubt.identityservice.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ubt.identityservice.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByEmailAndEnabled(String email, boolean enabled);
    List<User> findAllByEnabled(boolean enabled);
}
