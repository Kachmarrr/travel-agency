package com.epam.finaltask.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import com.epam.finaltask.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long userId);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);

    List<User> findUsersByRole(Role role);

    boolean existsByUsername(String username);
}
