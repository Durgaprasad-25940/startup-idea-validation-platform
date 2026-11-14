package com.startup.validator.repository;

import com.startup.validator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository is an interface that provides CRUD operations on the 'users' table.
 * 
 * Explanation of Annotations and Extensions:
 * `@Repository`: Tells Spring that this interface is a repository component and enables automatic exception translation.
 * `JpaRepository<User, Long>`: Inherits standard Spring Data JPA methods like save(), findById(), deleteById(), findAll(), etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their unique username.
     * @param username the username to search for.
     * @return an Optional containing the User if found, or empty.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by their unique email.
     * @param email the email to search for.
     * @return an Optional containing the User if found, or empty.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user exists with the given username.
     * @param username the username to check.
     * @return true if exists, false otherwise.
     */
    Boolean existsByUsername(String username);

    /**
     * Check if a user exists with the given email address.
     * @param email the email to check.
     * @return true if exists, false otherwise.
     */
    Boolean existsByEmail(String email);
}
