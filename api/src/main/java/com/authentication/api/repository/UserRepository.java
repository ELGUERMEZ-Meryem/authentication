package com.authentication.api.repository;

import com.authentication.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByEmailAndIsEnabled(String email, Integer isEnabled);
}
